package com.computernetwork.filetransferserver.Model;

import java.sql.*;
import java.util.ArrayList;

public class ServerDatabase {
    private final Connection connection;
    public ServerDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:ServerDatabase.db");
        System.out.println("Connected to the database.");
        Statement statement = connection.createStatement();
        String createFileTable = "CREATE TABLE IF NOT EXISTS file_data (" +
                "name TEXT NOT NULL," +
                "file_size INTEGER NOT NULL," +
                "description TEXT NOT NULL," +
                "date TEXT NOT NULL," +
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
    public String getUserIP(String username) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT ip FROM user_data WHERE name = ?");
        ps.setString(1, username);
        ResultSet row = ps.executeQuery();
        String userIP = row.getString("ip");
        if(!row.wasNull()) return userIP;
        return null;
    }

    /**
     * Set the IP address of username to ipAddress, return true if successful, false if username doesn't exist
     */
    public boolean setUserIP(String username, String ipAddress) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM user_data WHERE name = ?");
        ps.setString(1, username);
        ResultSet row = ps.executeQuery();
        String s = row.getString("name");
        if(row.wasNull()) return false;
        row.updateString("ip", ipAddress);
        row.updateRow();
        return true;
    }

    /**
     * Insert a new user record in user_data, return true if successful, false if username already exist
     */
    public boolean insertUser(String username, String ipAddress) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM user_data WHERE name = ?");
        ps.setString(1, username);
        ResultSet row = ps.executeQuery();
        String s = row.getString("name");

        if(!row.wasNull()) return false;
        
        row.moveToInsertRow();
        row.updateString("name", username);
        row.updateString("ip", ipAddress);
        row.insertRow();
        return true;
    }
    /**
     * Insert a new file data record in file_data, return true if successful, false if file already exist under the same owner
     */
    public boolean insertFile(FileData fileData) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM file_data WHERE name = ? AND owner = ?");
        ps.setString(1, fileData.getName());
        ps.setString(2, fileData.getOwner());
        ResultSet row = ps.executeQuery();
        String s = row.getString("name");

        if(!row.wasNull()) return false;
        
        row.moveToInsertRow();
        row.updateString("name", fileData.getName());
        row.updateLong("file_size", fileData.getSize());
        row.updateString("description", fileData.getDescription());
        row.updateString("date", fileData.getUploadedDate().toString());
        row.updateString("owner", fileData.getOwner());
        row.insertRow();
        return true;
    }

    /**
     * Query file by name
     */
    public ArrayList<FileData> searchFile(String query) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM file_data WHERE name LIKE %?%");
        ps.setString(1, query);
        ResultSet row = ps.executeQuery();

        ArrayList<FileData> fileList = new ArrayList<>();

        while(row.next()) {
            String name = row.getString("name");
            Long file_size = row.getLong("file_size");
            String description = row.getString("description");
            Date date = Date.valueOf(row.getString("date"));
            String owner = row.getString("owner");

            FileData fileData = new FileData(name, file_size, description, date, owner);

            fileList.add(fileData);
        }
        return fileList;
    }
    /**
     * Update file date of a specific user, delete all old file data and insert the new one
     */
    public boolean updateFile(ArrayList<FileData> fileData, String owner) throws SQLException {
        PreparedStatement ps1 = connection.prepareStatement("DELETE FROM file_data WHERE owner = ?");
        ps1.setString(1, owner);
        ps1.executeQuery();

        PreparedStatement ps2 = connection.prepareStatement("INSERT INTO file_data VALUES (?,?,?,?,?)");

        for (FileData file : fileData) {
            ps2.setString(1, file.getName());
            ps2.setLong(2, file.getSize());
            ps2.setString(3, file.getDescription());
            ps2.setString(4, file.getUploadedDate().toString());
            ps2.setString(5, file.getOwner());

            ps2.executeQuery();
        }
        return true;
    }
}
