package com.computernetwork.filetransferserver.Model;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkListener {
    private LocalDatabase database;
    private TextArea output;

    public NetworkListener(LocalDatabase database, TextArea output) {
        this.database = database;
        this.output = output;
    }
    public void start() {
        Task startTask = new Task() {
            @Override
            protected String call() throws Exception {
                ServerSocket socket = new ServerSocket(4040);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        output.appendText("Server started, waiting for connection\n");
                    }
                });
                while (!isCancelled()) {
                    Socket client = socket.accept();
                    Task task = new Task() {
                        @Override
                        protected String call() throws Exception {
                            DataInputStream istream = new DataInputStream(client.getInputStream());
                            DataOutputStream ostream = new DataOutputStream(client.getOutputStream());

                            //TODO: Read and process incomming request

                            return null;
                        }
                        @Override
                        protected void succeeded() {
                            output.appendText("Task succeeded");
                        }
                        @Override
                        protected void failed() {
                            output.appendText("Task failed");
                        }
                    };
                    Thread t = new Thread(task);
                    t.setDaemon(true);
                    t.start();
                }
                return "Server stopped";
            }
            @Override
            protected void failed() {
                output.appendText("Cannot start server\n");
            }
        };
        Thread startThread = new Thread(startTask);
        startThread.setDaemon(true);
        startThread.start();
    }
}
