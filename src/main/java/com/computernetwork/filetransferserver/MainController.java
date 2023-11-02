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
        listener = new NetworkListener(database, output);
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
        String[] tokens = cmd.split(" ");
        if (tokens.length == 0) return null;
        switch (tokens[0]) {
            case "start":
                if (listener.isStarted()) return "Server already started";
                listener.start();
                return "Starting server...";
            case "ping":
                if (tokens.length == 1) return "Not enough parameters";
                try {
                    String userIP = database.getUserIP(tokens[1]);
                    if (userIP == null) return "Username does not exist";
                    output.appendText("Pinging " + userIP + "\n");
                    Task<Respond> task = NetworkSender.ping(userIP, tokens[1]);
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
                if (tokens.length == 1) return "Not enough parameters";
                try {
                    String userIP = database.getUserIP(tokens[1]);
                    if (userIP == null) return "Username does not exist";
                    ArrayList<ClientFileData> fileList = new ArrayList<>();
                    Task<Respond> task = NetworkSender.discover(userIP, tokens[1], fileList);
                    task.setOnSucceeded(event -> {
                        if (task.getValue().isSuccess()) {
                            output.appendText("Discover successful, returned file list:\n");
                            for (ClientFileData file: fileList) {
                                output.appendText(file.getName() + " " + file.getSize() + " " + file.getDescription() + " " + file.getFileLocation() + "\n");
                            }
                            try {
                                database.checkFile(tokens[1], fileList);
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
    public void onClose() throws SQLException, IOException {
        if (database != null) database.close();
    }
}