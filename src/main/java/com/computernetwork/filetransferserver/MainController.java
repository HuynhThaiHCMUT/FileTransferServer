package com.computernetwork.filetransferserver;

import com.computernetwork.filetransferserver.Model.LocalDatabase;
import com.computernetwork.filetransferserver.Model.NetworkListener;
import com.computernetwork.filetransferserver.Model.NetworkSender;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class MainController {
    private LocalDatabase database;
    private NetworkListener listener;
    private NetworkSender sender;
    @FXML
    private TextField input;
    @FXML
    private TextArea output;
    @FXML
    private void initialize() {
        try {
            database = new LocalDatabase();
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
        switch (tokens[0]) {
            case "start":
                listener.start();
                return "Starting server...";

                //TODO: Add more command

            default:
                return "Invalid command";
        }
    }
}