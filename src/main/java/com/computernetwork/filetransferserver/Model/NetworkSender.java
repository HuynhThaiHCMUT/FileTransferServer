package com.computernetwork.filetransferserver.Model;

import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.*;
import java.util.ArrayList;

public class NetworkSender {
    public static Task<Respond> ping(String userIP) {
        return new Task<Respond>() {
            @Override
            protected Respond call() throws Exception {
                Socket socket = new Socket(userIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                //TODO

                socket.close();
                return null;
            }
        };
    }
    public static Task<ArrayList<FileData>> discover(String userIP) {
        return new Task<ArrayList<FileData>>() {
            @Override
            protected ArrayList<FileData> call() throws Exception {
                Socket socket = new Socket(userIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                //TODO

                socket.close();
                return null;
            }
        };
    }
}
