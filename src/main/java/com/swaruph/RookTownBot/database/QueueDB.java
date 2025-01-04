package com.swaruph.RookTownBot.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.swaruph.RookTownBot.model.Queue;

public class QueueDB {

    public void insertIntoQueue(int queueId, String queueName, String queueType, String queueStatus, String queueAdminId) throws SQLException {
        String query = "INSERT INTO queues (queue_id, queue_name, queue_type, queue_status, queue_admin) VALUES (?, ?, ?, ?, ?)";
        Connection con = Database.connect();
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
            Connection con = Database.connect();
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM queues ORDER BY queue_id DESC LIMIT 1");
            ResultSet resultSet = preparedStatement.executeQuery();
            lastId = resultSet.getInt("queue_id");
            preparedStatement.close();
            resultSet.close();
            con.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        if(lastId == 0){
            return 1;
        }
        return lastId;
    }

    public boolean getQueueStatus(int queue_id){
        boolean queue_status = false;
        try {
            Connection con = Database.connect();
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
            Connection con = Database.connect();
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
