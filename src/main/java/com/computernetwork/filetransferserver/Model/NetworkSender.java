package com.computernetwork.filetransferserver.Model;

import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class NetworkSender {
    public static Task<Respond> ping(String userIP, String username) {
        return new Task<>() {
            @Override
            protected Respond call() throws Exception {
                return blockingPing(userIP, username);
            }
        };
    }
    public static Task<Respond> discover(String userIP, String username, ArrayList<ClientFileData> fileList) {
        return new Task<>() {
            @Override
            protected Respond call() throws Exception {
                return blockingDiscover(userIP, username, fileList);
            }
        };
    }

    public static Respond blockingPing(String userIP, String username) throws IOException {
        Socket socket = new Socket(userIP, 4041);
        DataInputStream istream = new DataInputStream(socket.getInputStream());
        DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

        socket.setSoTimeout(5000);

        try {
            ostream.writeShort(0);
            ostream.writeUTF(username);

            short respondCode = istream.readShort();
            Respond respond = new Respond(false, null);
            if (respondCode == 200) {
                respond.setSuccess(true);
                respond.setMessage("Client is online");
            } else if (respondCode == 401) {
                respond.setMessage(username + "doesn't match with " + userIP);
            } else{
                respond.setMessage("Invalid respond code: " + respondCode);
            }

            socket.close();
            return respond;
        } catch (SocketTimeoutException e) {
            socket.close();
            throw new SocketTimeoutException("Request timed out");
        }
    }

    public static Respond blockingDiscover(String userIP, String username, ArrayList<ClientFileData> returnedFileList) throws IOException {
        Socket socket = new Socket(userIP, 4041);
        DataInputStream istream = new DataInputStream(socket.getInputStream());
        DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

        socket.setSoTimeout(5000);

        try {
            ostream.writeShort(6);
            ostream.writeUTF(username);

            short respondCode = istream.readShort();
            Respond respond = new Respond(false, null);
            if (respondCode == 200) {
                respond.setSuccess(true);
                respond.setMessage("Discover successful");
                short fileCount = istream.readShort();

                long fileSize;
                String filename;
                String fileDescription;
                String localFilename;

                for(int i = 0; i<fileCount; i++){
                    fileSize = istream.readLong();
                    filename = istream.readUTF();
                    fileDescription = istream.readUTF();
                    localFilename = istream.readUTF();

                    ClientFileData file = new ClientFileData(filename, fileSize, fileDescription, localFilename);
                    returnedFileList.add(file);
                }
            } else if (respondCode == 401) {
                respond.setMessage(username + "doesn't match with " + userIP);
            } else{
                respond.setMessage("Invalid respond code: " + respondCode);
            }
            socket.close();
            return respond;
        } catch (SocketTimeoutException e) {
            socket.close();
            throw new SocketTimeoutException("Request timed out");
        }
    }
}
