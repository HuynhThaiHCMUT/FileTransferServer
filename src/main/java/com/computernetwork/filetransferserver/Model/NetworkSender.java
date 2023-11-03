package com.computernetwork.filetransferserver.Model;

import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class NetworkSender {
    public static Task<Response> ping(String userIP, String username) {
        return new Task<>() {
            @Override
            protected Response call() throws Exception {
                return blockingPing(userIP, username);
            }
        };
    }
    public static Task<Response> discover(String userIP, String username, ArrayList<ClientFileData> fileList) {
        return new Task<>() {
            @Override
            protected Response call() throws Exception {
                return blockingDiscover(userIP, username, fileList);
            }
        };
    }

    public static Response blockingPing(String userIP, String username) throws IOException {
        Socket socket = new Socket(userIP, 4041);
        DataInputStream istream = new DataInputStream(socket.getInputStream());
        DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

        socket.setSoTimeout(5000);

        try {
            ostream.writeShort(0);
            ostream.writeUTF(username);

            short respondCode = istream.readShort();
            Response response = new Response(false, null);
            if (respondCode == 200) {
                response.setSuccess(true);
                response.setMessage("Client is online");
            } else if (respondCode == 401) {
                response.setMessage(username + "doesn't match with " + userIP);
            } else{
                response.setMessage("Invalid response code: " + respondCode);
            }

            socket.close();
            return response;
        } catch (SocketTimeoutException e) {
            socket.close();
            throw new SocketTimeoutException("Request timed out");
        }
    }

    public static Response blockingDiscover(String userIP, String username, ArrayList<ClientFileData> returnedFileList) throws IOException {
        Socket socket = new Socket(userIP, 4041);
        DataInputStream istream = new DataInputStream(socket.getInputStream());
        DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

        socket.setSoTimeout(5000);

        try {
            ostream.writeShort(6);
            ostream.writeUTF(username);

            short respondCode = istream.readShort();
            Response response = new Response(false, null);
            if (respondCode == 200) {
                response.setSuccess(true);
                response.setMessage("Discover successful");
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
                response.setMessage(username + "doesn't match with " + userIP);
            } else{
                response.setMessage("Invalid response code: " + respondCode);
            }
            socket.close();
            return response;
        } catch (SocketTimeoutException e) {
            socket.close();
            throw new SocketTimeoutException("Request timed out");
        }
    }
}
