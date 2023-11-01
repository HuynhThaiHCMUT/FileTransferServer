package com.computernetwork.filetransferserver.Model;

import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NetworkSender {
    public static Task<Respond> ping(String userIP, String username) {
        return new Task<Respond>() {
            @Override
            protected Respond call() throws Exception {
                Socket socket = new Socket(userIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                ostream.writeShort(0);

                int usernameLength = username.length();
                ostream.writeShort(usernameLength);
                ostream.writeUTF(username);

                short respondCode = istream.readShort();
                Respond respondmsg = new Respond(false, null);
                if (respondCode >= 100 && respondCode < 600) {

                    if (respondCode == 200) {
                        respondmsg.setSuccess(true);
                        respondmsg.setMessage("Client is now online");
                    }
                    else if (respondCode == 401) {
                        respondmsg.setMessage(username + "doesn't match with " + userIP);
                    }
                }
                else{
                    respondmsg.setMessage("Invalid respondcode");
                }
                socket.close();

                return respondmsg;
            }
        };
    }
    public static Task<ArrayList<FileData>> discover(String userIP, String username) {
        return new Task<ArrayList<FileData>>() {
            @Override
            protected ArrayList<FileData> call() throws Exception {
                Socket socket = new Socket(userIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                ostream.writeShort(6);

                int usernameLength = username.length();
                ostream.writeShort(usernameLength);
                ostream.writeUTF(username);

                short respondCode = istream.readShort();

                ArrayList<FileData> fileDatalist = new ArrayList<FileData>(null);
                if (respondCode>= 100 && respondCode <600) {
                    istream.readShort();

                    if (respondCode == 200) {
                        short fileCount = istream.readShort();

                        byte[] buffer;
                        long fileSize;
                        short fileNameLength;
                        String filename;
                        short fileDescriptionLength;
                        String fileDescription;
                        short localFilenameLength;
                        String localFilename;

                        for(int i = 0; i<fileCount; i++){
                            fileSize = istream.readLong();

                            fileNameLength = istream.readShort();
                            buffer = istream.readNBytes(fileNameLength);
                            filename = new String(buffer, StandardCharsets.UTF_8);

                            fileDescriptionLength = istream.readShort();
                            buffer = istream.readNBytes(fileDescriptionLength);
                            fileDescription = new String(buffer, StandardCharsets.UTF_8);

                            localFilenameLength = istream.readShort();
                            buffer = istream.readNBytes(localFilenameLength);
                            localFilename = new String(buffer, StandardCharsets.UTF_8);

                            FileData file = new FileData(filename, fileSize, fileDescription, username);
                            fileDatalist.add(file);
                        }

                    }  
                }  
                socket.close();
                return fileDatalist;
            }
        };
    }
}
