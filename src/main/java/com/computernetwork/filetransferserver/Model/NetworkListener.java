package com.computernetwork.filetransferserver.Model;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NetworkListener {
    private ServerDatabase database;
    private TextArea output;

    public NetworkListener(ServerDatabase database, TextArea output) {
        this.database = database;
        this.output = output;
    }
    public void start() {
        Task<String> startTask = new Task<>() {
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
                    Socket newClient = socket.accept();
                    ListenerTask task = new ListenerTask(newClient);
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
    class ListenerTask extends Task<String> {
        private final Socket client;
        private final String clientIP;

        public ListenerTask(Socket newClient) {
            client = newClient;
            clientIP = client.getInetAddress().getHostAddress();
        }
        @Override
        protected String call() throws Exception {
            DataInputStream istream = new DataInputStream(client.getInputStream());
            DataOutputStream ostream = new DataOutputStream(client.getOutputStream());

            short msgType = istream.readShort();
            if (msgType < 0 || msgType >= 100) {
                ostream.writeShort(300);
                ostream.writeShort(msgType);
                return clientIP + " sent a request\n" +
                        "Incorrect request, msgType was: " + msgType + "\n";
            }

            short usernameLength = istream.readShort();
            if (usernameLength < 0) {
                ostream.writeShort(400);
                ostream.writeShort(msgType);
                return clientIP + " sent a request\n" +
                        "Incorrect request, usernameLength was: " + usernameLength + "\n";
            }

            byte[] buffer;
            buffer = istream.readNBytes(usernameLength);
            String username = new String(buffer, StandardCharsets.UTF_8);
            switch (msgType) {
                case 1:
                    if (database.setUserIP(username, clientIP)) {
                        ostream.writeShort(200);
                        ostream.writeShort(msgType);
                        return clientIP + " sent a login request as " + username + "\n" +
                                "Login successful\n";
                    } else {
                        ostream.writeShort(401);
                        ostream.writeShort(msgType);
                        return clientIP + " sent a login request as " + username + "\n" +
                                "Login failed, username doesn't exist\n";
                    }
                case 2:
                    if (database.insertUser(username, clientIP)) {
                        ostream.writeShort(200);
                        ostream.writeShort(msgType);
                        return clientIP + " sent a sign up request as " + username + "\n" +
                                "Sign up successful\n";
                    } else {
                        ostream.writeShort(401);
                        ostream.writeShort(msgType);
                        return clientIP + " sent a login request as " + username + "\n" +
                                "Sign up failed, username already exist\n";
                    }
                case 3:
                    if (!database.setUserIP(username, clientIP)) {
                        ostream.writeShort(401);
                        ostream.writeShort(msgType);
                        return clientIP + " sent an upload request as " + username + "\n" +
                                "Upload failed, username doesn't exist\n";
                    }

                    long fileSize = istream.readLong();
                    if (fileSize < 0) {
                        ostream.writeShort(402);
                        ostream.writeShort(msgType);
                        return clientIP + " sent an upload request as " + username + "\n" +
                                "Incorrect request, fileSize was: " + fileSize + "\n";
                    }

                    short fileNameLength = istream.readShort();
                    if (fileNameLength < 0) {
                        ostream.writeShort(403);
                        ostream.writeShort(msgType);
                        return clientIP + " sent an upload request as " + username + "\n" +
                                "Incorrect request, fileNameLength was: " + fileNameLength + "\n";
                    }

                    buffer = istream.readNBytes(fileNameLength);
                    String fileName = new String(buffer, StandardCharsets.UTF_8);

                    short fileDescriptionLength = istream.readShort();
                    if (fileDescriptionLength < 0) {
                        ostream.writeShort(405);
                        ostream.writeShort(msgType);
                        return clientIP + " sent an upload request as " + username + "\n" +
                                "Incorrect request, fileDescriptionLength was: " + fileDescriptionLength + "\n";
                    }

                    buffer = istream.readNBytes(fileDescriptionLength);
                    String description = new String(buffer, StandardCharsets.UTF_8);

                    FileData fileData = new FileData(fileName, fileSize, description, username);
                    if (database.insertFile(fileData)) {
                        ostream.writeShort(200);
                        ostream.writeShort(msgType);
                        return clientIP + " sent an upload request as " + username + "\n" +
                                "Upload successful\n";
                    } else {
                        ostream.writeShort(404);
                        ostream.writeShort(msgType);
                        return clientIP + " sent an upload request as " + username + "\n" +
                                "Upload failed, file name already exist for username " + username + "\n";
                    }
                case 4:
                    if (!database.setUserIP(username, clientIP)) {
                        ostream.writeShort(401);
                        ostream.writeShort(msgType);
                        return clientIP + " sent a search request as " + username + "\n" +
                                "Incorrect request, username doesn't exist\n";
                    }

                    short queryLength = istream.readShort();
                    buffer = istream.readNBytes(queryLength);
                    String query = new String(buffer, StandardCharsets.UTF_8);
                    if (queryLength < 0) {
                        ostream.writeShort(406);
                        ostream.writeShort(msgType);
                        return clientIP + " sent a search request as " + username + "\n" +
                                "Incorrect request, queryLength was: " + queryLength + "\n";
                    }
                    ArrayList<FileData> fileArray = database.searchFile(query);
                    ostream.writeShort(200);
                    ostream.writeShort(msgType);
                    for (FileData file: fileArray) {
                        ostream.writeShort(file.getOwner().length());
                        ostream.writeUTF(file.getOwner());
                        ostream.writeLong(file.getSize());
                        ostream.writeShort(file.getName().length());
                        ostream.writeUTF(file.getName());
                        ostream.writeShort(file.getDescription().length());
                        ostream.writeUTF(file.getDescription());
                        ostream.writeLong(file.getUploadedDate().getTime());
                    }
                    return clientIP + " sent a search request with query=" + query + " as " + username + "\n" +
                            "Returned" + fileArray.size() + "search result(s)\n";
                case 5:
                    short reportedFileNameLength = istream.readShort();
                    if (reportedFileNameLength < 0) {
                        ostream.writeShort(403);
                        ostream.writeShort(msgType);
                        return clientIP + " sent an report missing file request of " + username + "\n" +
                                "Incorrect request, reportedFileNameLength was: " + reportedFileNameLength + "\n";
                    }

                    buffer = istream.readNBytes(reportedFileNameLength);
                    String reportedFileName = new String(buffer, StandardCharsets.UTF_8);

                    ostream.writeShort(200);
                    ostream.writeShort(msgType);

                    //TODO: Do something with file name

                    String userIP = database.getUserIP(username);
                    NetworkSender.discover(userIP, username);
                default:
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
                client.close();
            } catch (IOException e) {
                output.appendText("Error while closing client connection: " + e.getMessage() + "\n");
            }
            output.appendText("Error while handling request from " + clientIP + ": " + getException().getMessage() + "\n");
        }
    };
}
