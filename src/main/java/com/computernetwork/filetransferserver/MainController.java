package com.computernetwork.filetransferserver;

import com.computernetwork.filetransferserver.Model.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController {
    private ServerDatabase database;
    private NetworkListener listener;
    @FXML
    private TextField input;
    @FXML
    private TextArea output;
    @FXML
    private void initialize() {
        try {
            database = new ServerDatabase();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setContentText("Can't connect to database");
            alert.showAndWait();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        output.appendText("Type help for a list of command\n");
        listener = new NetworkListener(database, output);
        listener.start();
    }
    @FXML
    private void onKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            output.appendText(input.getText() + "\n");
            String result = process(input.getText());
            if (result != null) output.appendText(result + "\n");
            input.clear();
        }
    }
    private String process(String cmd) {
        ArrayList<String> tokens = splitTokens(cmd);
        if (tokens.isEmpty()) return null;
        switch (tokens.get(0)) {
            case "help":
                return """
                        Command format: command "parameter1" "parameter2" ...
                        List of command:
                        > start: start the server
                        > stop: stop the server
                        > clear: clear the command-line output
                        > ping "hostname": check if the host with hostname is online
                        > discover "hostname": check the local files of host hostname""";
            case "start":
                if (listener.isStarted()) return "Server already started";
                listener.start();
                return "Starting server...";
            case "clear":
                output.clear();
                return null;
            case "ping":
                if (tokens.size() == 1) return "Not enough parameters";
                try {
                    String userIP = database.getUserIP(tokens.get(1));
                    if (userIP == null) return "Username does not exist";
                    output.appendText("Pinging " + userIP + "\n");
                    Task<Response> task = NetworkSender.ping(userIP, tokens.get(1));
                    task.setOnSucceeded(event -> output.appendText(task.getValue().getMessage() + "\n"));
                    task.setOnFailed(event -> output.appendText("Failed to ping user: " + task.getException().getMessage() + "\n"));
                    Thread t = new Thread(task);
                    t.setDaemon(true);
                    t.start();
                    return null;
                } catch (SQLException e) {
                    return "Error while getting userIP from username";
                }
            case "discover":
                if (tokens.size() == 1) return "Not enough parameters";
                try {
                    String userIP = database.getUserIP(tokens.get(1));
                    if (userIP == null) return "Username does not exist";
                    ArrayList<ClientFileData> fileList = new ArrayList<>();
                    Task<Response> task = NetworkSender.discover(userIP, tokens.get(1), fileList);
                    task.setOnSucceeded(event -> {
                        if (task.getValue().isSuccess()) {
                            output.appendText("Discover successful, returned file list:\n");
                            for (ClientFileData file: fileList) {
                                output.appendText(file.getName() + " " + file.getSize() + " " + file.getDescription() + " " + file.getFileLocation() + "\n");
                            }
                            try {
                                database.checkFile(tokens.get(1), fileList);
                            } catch (SQLException e) {
                                output.appendText("Failed to update database\n");
                            }
                        } else {
                            output.appendText("Discover failed: " + task.getValue().getMessage() + "\n");
                        }
                    });
                    task.setOnFailed(event -> output.appendText("Discover failed: " + task.getException().getMessage() + "\n"));
                    Thread t = new Thread(task);
                    t.setDaemon(true);
                    t.start();
                    return null;
                } catch (SQLException e) {
                    return "Error while getting userIP from username";
                }
            case "stop":
                listener.stop();
                return "Server stopped";
            default:
                return "Invalid command";
        }
    }
    public static ArrayList<String> splitTokens(String input) {
        ArrayList<String> tokens = new ArrayList<>();

        // Regular expression to match tokens with or without double quotes
        Pattern pattern = Pattern.compile("([^\"]\\S*|\"*\\S*\")\\s*");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String token = matcher.group(1);

            // Special case
            if (token.equals("\"")) tokens.add("");
            // Remove double quotes if present
            else if (token.startsWith("\"") && token.endsWith("\"")) {
                token = token.substring(1, token.length() - 1);
            }

            tokens.add(token);
        }
        return tokens;
    }
    public void onClose() throws SQLException, IOException {
        if (database != null) database.close();
    }
}