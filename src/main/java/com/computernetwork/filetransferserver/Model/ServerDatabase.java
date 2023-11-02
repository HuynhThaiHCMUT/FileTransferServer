package com.computernetwork.filetransferserver.Model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

        if (row.next()) {
            return row.getString("ip");
        }
        return null;
    }

    /**
     * Set the IP address of username to ipAddress, return true if successful, false if username doesn't exist
     */
    public boolean setUserIP(String username, String ipAddress) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM user_data WHERE name = ?");
        ps.setString(1, username);
        ResultSet row = ps.executeQuery();
        if (row.next()) {
            String s = row.getString("name");
            if (s != null) {
                String updateSql = "UPDATE user_data SET ip = ? WHERE name = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setString(1, ipAddress);
                updateStatement.setString(2, username);
                int rowsUpdated = updateStatement.executeUpdate();

                return rowsUpdated > 0;
            }
        }
        return false;
    }

    /**
     * Insert a new user record in user_data, return true if successful, false if username already exist
     */
    public boolean insertUser(String username, String ipAddress) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM user_data WHERE name = ?");
        ps.setString(1, username);
        ResultSet row = ps.executeQuery();

        if (row.next()) return false;

        // If the row doesn't exist, insert a new row
        PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO user_data (name, ip) VALUES (?, ?)");
        insertStatement.setString(1, username);
        insertStatement.setString(2, ipAddress);
        insertStatement.executeUpdate();

        return true;
    }
    /**
     * Insert a new file data record in file_data, return true if successful, false if file already exist under the same owner
     */
    public boolean insertFile(ServerFileData fileData) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM file_data WHERE name = ? AND owner = ?");
        ps.setString(1, fileData.getName());
        ps.setString(2, fileData.getOwner());
        ResultSet row = ps.executeQuery();

        if (row.next()) return false;

        PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO file_data (name, file_size, description, date, owner) VALUES (?, ?, ?, ?, ?)");
        insertStatement.setString(1, fileData.getName());
        insertStatement.setLong(2, fileData.getSize());
        insertStatement.setString(3, fileData.getDescription());
        insertStatement.setString(4, fileData.getUploadedDate().toString());
        insertStatement.setString(5, fileData.getOwner());
        insertStatement.executeUpdate();

        return true;
    }

    /**
     * Search for file by name
     */
    public ArrayList<ServerFileData> searchFile(String query, String username) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM file_data WHERE name LIKE ? AND owner <> ?");
        ps.setString(1, "%" + query + "%");
        ps.setString(2, username);
        ResultSet row = ps.executeQuery();

        ArrayList<ServerFileData> fileList = new ArrayList<>();

        while(row.next()) {
            String name = row.getString("name");
            long file_size = row.getLong("file_size");
            String description = row.getString("description");
            Date date = Date.valueOf(row.getString("date"));
            String owner = row.getString("owner");

            ServerFileData fileData = new ServerFileData(name, file_size, description, date, owner);
            fileList.add(fileData);
        }
        return fileList;
    }

    /**
     * go through the list of file of a user, delete them from the file_data table if they're no longer exist
     */
    public void checkFile(String username, ArrayList<ClientFileData> fileList) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM file_data WHERE owner = ?");
        statement.setString(1, username);
        statement.executeUpdate();

        for (ClientFileData file: fileList) {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO file_data VALUES (?,?,?,?,?)");
            ps.setString(1, file.getName());
            ps.setLong(2, file.getSize());
            ps.setString(3, file.getDescription());
            ps.setString(4, file.getUploadedDate().toString());
            ps.setString(5, username);

            ps.executeUpdate();
        }
    }
}
