package com.swaruph.RookTownBot.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.swaruph.RookTownBot.config.DatabaseConfig;

public class QueueDB {
    DatabaseConfig db = new DatabaseConfig();

    public void createQueueTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS queues (queue_id INTEGER PRIMARY KEY, queue_name TEXT, queue_type TEXT, queue_status BOOLEAN, queue_admin TEXT)";
        Connection con = db.connect();
        con.createStatement().execute(query);
        con.close();
    }

    public void insertIntoQueue(int queueId, String queueName, String queueType, String queueStatus, String queueAdminId) throws SQLException {
        String query = "INSERT INTO queues (queue_id, queue_name, queue_type, queue_status, queue_admin) VALUES (?, ?, ?, ?, ?)";
        Connection con = db.connect();
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setInt(1, queueId);
        pstmt.setString(2, queueName);
        pstmt.setString(3, queueType);
        pstmt.setString(4, queueStatus);
        pstmt.setString(5, queueAdminId);
        pstmt.executeUpdate();
        pstmt.close();
        con.close();
    }

    public int getLastRowInQueue() throws SQLException {
        int lastId = 0;
        try {
            Connection con = db.connect();
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM queues ORDER BY queue_id DESC LIMIT 1");
            ResultSet resultSet = preparedStatement.executeQuery();
            lastId = resultSet.getInt("queue_id");
            preparedStatement.close();
            resultSet.close();
            con.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return lastId;
    }

    public boolean getQueueStatus(int queue_id){
        boolean queue_status = false;
        try {
            Connection con = db.connect();
            PreparedStatement statement = con.prepareStatement("SELECT (queueStatus) FROM queues WHERE queue_id="+ queue_id);
            ResultSet resultSet = statement.getResultSet();
            queue_status = resultSet.getBoolean("queue_status");

        }catch (SQLException e){
            e.printStackTrace();
        }
        return queue_status;
    }

    public void setQueueStatus(int queue_id, boolean status){
        String query = "UPDATE queues SET queue_status = ? WHERE queue_id = ?";
        try {
            Connection con = db.connect();
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setBoolean(1, status);
            pstmt.setInt(2, queue_id);
            pstmt.executeUpdate();
            pstmt.close();
            con.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
