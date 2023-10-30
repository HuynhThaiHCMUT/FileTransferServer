package com.computernetwork.filetransferserver.Model;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

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
    public static Task<ObservableList<FileData>> discover(String userIP) {
        return new Task<ObservableList<FileData>>() {
            @Override
            protected ObservableList<FileData> call() throws Exception {
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
