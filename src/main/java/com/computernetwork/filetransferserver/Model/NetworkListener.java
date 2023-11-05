package com.computernetwork.filetransferserver.Model;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class NetworkListener {
    private final ServerDatabase database;
    private final TextArea output;
    public ServerSocket socket;
    private boolean started;
    public NetworkListener(ServerDatabase database, TextArea output) {
        this.database = database;
        this.output = output;
        started = false;
    }
    public void start() {
        try {
            socket = new ServerSocket(4040);
        } catch (IOException e) {
            output.appendText("Error while starting server: " + e.getMessage());
            return;
        }
        Task<String> startTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                started = true;
                Platform.runLater(() -> output.appendText("Server started, waiting for connection\n"));
                while (!isCancelled()) {
                    Socket newClient = socket.accept();
                    ListenerTask task = new ListenerTask(newClient);
                    Thread t = new Thread(task);
                    t.setDaemon(true);
                    t.start();
                }
                return "Server stopped\n";
            }
            @Override
            protected void succeeded() {
                try {
                    started = false;
                    socket.close();
                } catch (IOException e) {
                    output.appendText("Error while closing server: " + e.getMessage() + "\n");
                }
                output.appendText(getValue());
            }
            @Override
            protected void failed() {
                try {
                    started = false;
                    socket.close();
                } catch (IOException e) {
                    output.appendText("Error while closing server: " + e.getMessage() + "\n");
                }
                output.appendText("Server caught an exception: " + getException().getMessage() + "\n");
            }
        };
        Thread startThread = new Thread(startTask);
        startThread.setDaemon(true);
        startThread.start();
    }
    class ListenerTask extends Task<String> {
        private final Socket client;
        private final String clientIP;
        private DataInputStream istream;
        private DataOutputStream ostream;

        public ListenerTask(Socket newClient) {
            client = newClient;
            clientIP = client.getInetAddress().getHostAddress();
        }
        @Override
        protected String call() throws Exception {
            istream = new DataInputStream(client.getInputStream());
            ostream = new DataOutputStream(client.getOutputStream());

            short msgType = istream.readShort();
            String username = istream.readUTF();

            switch (msgType) {
                case 1:
                    if (database.setUserIP(username, clientIP)) {
                        ostream.writeShort(200);
                        return clientIP + " sent a login request as " + username + "\n" +
                                "Login successful\n";
                    } else {
                        ostream.writeShort(401);
                        return clientIP + " sent a login request as " + username + "\n" +
                                "Login failed, username doesn't exist\n";
                    }
                case 2:
                    if (database.insertUser(username, clientIP)) {
                        ostream.writeShort(200);
                        return clientIP + " sent a sign up request as " + username + "\n" +
                                "Sign up successful\n";
                    } else {
                        ostream.writeShort(401);
                        return clientIP + " sent a login request as " + username + "\n" +
                                "Sign up failed, username already exist\n";
                    }
                case 3:
                    if (!database.setUserIP(username, clientIP)) {
                        ostream.writeShort(401);
                        return clientIP + " sent an upload request as " + username + "\n" +
                                "Upload failed, username doesn't exist\n";
                    }

                    long fileSize = istream.readLong();
                    if (fileSize < 0) {
                        ostream.writeShort(402);
                        return clientIP + " sent an upload request as " + username + "\n" +
                                "Invalid request, fileSize was: " + fileSize + "\n";
                    }

                    String fileName = istream.readUTF();
                    String description = istream.readUTF();

                    ServerFileData fileData = new ServerFileData(fileName, fileSize, description, username);
                    if (database.insertFile(fileData)) {
                        ostream.writeShort(200);
                        return clientIP + " sent an upload request as " + username + "\n" +
                                "Upload successful\n";
                    } else {
                        ostream.writeShort(403);
                        return clientIP + " sent an upload request as " + username + "\n" +
                                "Upload failed, file name already exist for username " + username + "\n";
                    }
                case 4:
                    if (!database.setUserIP(username, clientIP)) {
                        ostream.writeShort(401);
                        return clientIP + " sent a search request as " + username + "\n" +
                                "Invalid request, username doesn't exist\n";
                    }

                    String query = istream.readUTF();

                    ArrayList<ServerFileData> fileArray = database.searchFile(query, username);
                    ostream.writeShort(200);
                    ostream.writeShort(fileArray.size());
                    for (ServerFileData file: fileArray) {
                        String userIP = database.getUserIP(file.getOwner());
                        if (userIP != null) {
                            Response response;
                            try {
                                response = NetworkSender.blockingPing(userIP, file.getOwner());
                            } catch (IOException e) {
                                response = new Response(false, null);
                            }
                            ostream.writeUTF(file.getOwner());
                            ostream.writeUTF(userIP);
                            ostream.writeBoolean(response.isSuccess());
                            ostream.writeLong(file.getSize());
                            ostream.writeUTF(file.getName());
                            ostream.writeUTF(file.getDescription());
                            ostream.writeLong(file.getUploadedDate().getTime());
                        }
                    }
                    return clientIP + " sent a search request with query = " + query + " as " + username + "\n" +
                            "Returned " + fileArray.size() + " search result(s)\n";
                case 5:
                    String userIP = database.getUserIP(username);
                    if (userIP == null) {
                        ostream.writeShort(401);
                        return clientIP + " sent an update file request for: " + username + "\n" +
                                "Invalid request, username does not exist\n";
                    }

                    ostream.writeShort(200);
                    ArrayList<ClientFileData> fileList = new ArrayList<>();
                    try {
                        Response response = NetworkSender.blockingDiscover(userIP, username, fileList);
                        if (response.isSuccess()) {
                            output.appendText("Discover successful, returned file list:\n");
                            for (ClientFileData file: fileList) {
                                output.appendText(file.getName() + " " + file.getSize() + " " + file.getDescription() + " " + file.getFileLocation() + "\n");
                            }
                            database.checkFile(username, fileList);
                        } else {
                            output.appendText("Discover failed: " + response.getMessage() + "\n");
                        }
                    } catch (IOException e) {
                        output.appendText("Discover failed: " + e.getMessage() + "\n");
                    }
                    return clientIP + " sent a update file request for: " + username + "\n" +
                            "Sending a discover request, to " + userIP +"\n";
                default:
                    ostream.writeShort(400);
                    ostream.writeShort(msgType);
                    return clientIP + " sent an invalid request (msgType = " + msgType +") as " + username + "\n";
            }
        }
        @Override
        protected void succeeded() {
            try {
                client.close();
            } catch (IOException e) {
                output.appendText("Error while closing client connection: " + e.getMessage() + "\n");
            }
            output.appendText(getValue());
        }
        @Override
        protected void failed() {
            try {
                ostream.writeShort(500);
                client.close();
            } catch (IOException e) {
                output.appendText("Error while closing client connection: " + e.getMessage() + "\n");
            }
            output.appendText("Error while handling request from " + clientIP + ": " + getException().getMessage() + "\n");
        }
    }

    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            output.appendText("Error while closing socket: " + e.getMessage());
        }
    }

    public boolean isStarted() {
        return started;
    }

}
