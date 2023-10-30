package com.computernetwork.filetransferserver.Model;

import java.sql.*;

public class LocalDatabase {
    private Connection connection;
    public LocalDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:ServerDatabase.db");
        System.out.println("Connected to the database.");
        Statement statement = connection.createStatement();
        String createFileTable = "CREATE TABLE IF NOT EXISTS file_data (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "file_size INTEGER NOT NULL," +
                "description TEXT NOT NULL," +
                "date INTEGER NOT NULL," +
                "owner TEXT NOT NULL," +
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
     * Set the IP address of username to ipAddress
     */
    public boolean setUserIP(String username, String ipAddress) {
        //TODO
        return false;
    }

    /**
     * Insert a new user record in user_data
     */
    public boolean insertUser(String username, String ipAddress) {
        //TODO
        return false;
    }
    /**
     * Insert a new file data record in file_data
     */
    public boolean insertFile(FileData fileData) {
        //TODO
        return false;
    }

    //TODO: Add more database operation
}
