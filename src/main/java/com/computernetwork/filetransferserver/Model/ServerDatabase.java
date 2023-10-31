package com.computernetwork.filetransferserver.Model;

import java.sql.*;
import java.util.ArrayList;

public class ServerDatabase {
    private Connection connection;
    public ServerDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:ServerDatabase.db");
        System.out.println("Connected to the database.");
        Statement statement = connection.createStatement();
        String createFileTable = "CREATE TABLE IF NOT EXISTS file_data (" +
                "name TEXT NOT NULL," +
                "file_size INTEGER NOT NULL," +
                "description TEXT NOT NULL," +
                "date INTEGER NOT NULL," +
                "owner TEXT NOT NULL," +
                "PRIMARY KEY (name, owner)" +
                ")";
        String createUserTable = "CREATE TABLE IF NOT EXISTS user_data (" +
                "name TEXT PRIMARY KEY," +
                "ip TEXT NOT NULL" +
                ")";
        statement.execute(createFileTable);
        statement.execute(createUserTable);
    }
    public void close() throws SQLException {
        connection.close();
        System.out.println("Connection closed");
    }
    /**
     * return the IP address of username, return null if there is none
     */
    public String getUserIP(String username) {
        //TODO
        return null;
    }

    /**
     * Set the IP address of username to ipAddress, return true if successful, false if username doesn't exist
     */
    public boolean setUserIP(String username, String ipAddress) {
        //TODO
        return false;
    }

    /**
     * Insert a new user record in user_data, return true if successful, false if username already exist
     */
    public boolean insertUser(String username, String ipAddress) {
        //TODO
        return false;
    }
    /**
     * Insert a new file data record in file_data, return true if successful, false if file already exist under the same owner
     */
    public boolean insertFile(FileData fileData) {
        //TODO
        return false;
    }

    /**
     * Query file by name
     */
    public ArrayList<FileData> queryFile(String query) {
        //TODO
        return null;
    }
    /**
     * Update file date of a specific user, delete all old file data and insert the new one
     */
    public boolean updateFile(ArrayList<FileData> fileData, String owner) {
        //TODO
        return false;
    }
}
