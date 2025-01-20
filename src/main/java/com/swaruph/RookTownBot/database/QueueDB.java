package com.swaruph.RookTownBot.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.swaruph.RookTownBot.config.DatabaseConfig;

public class QueueDB {

    DatabaseConfig db = new DatabaseConfig();

    public QueueDB(){
        createQueueTable();
    }

    public void createQueueTable() {
        String query = "CREATE TABLE IF NOT EXISTS queues (queue_id INTEGER PRIMARY KEY, queue_name TEXT, queue_type TEXT, queue_status BOOLEAN, queue_admin TEXT)";

        try(
                Connection con = db.connect();
                PreparedStatement pstmt = con.prepareStatement(query)
        ) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertIntoQueue(int queueId, String queueName, String queueType, boolean queueStatus, String queueAdminId) {
        String query = "INSERT INTO queues (queue_id, queue_name, queue_type, queue_status, queue_admin) VALUES (?, ?, ?, ?, ?)";

        try(
                Connection con = db.connect();
                PreparedStatement pstmt = con.prepareStatement(query)
        ) {
            pstmt.setInt(1, queueId);
            pstmt.setString(2, queueName);
            pstmt.setString(3, queueType);
            pstmt.setBoolean(4, queueStatus);
            pstmt.setString(5, queueAdminId);
            pstmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public int getLastRowInQueue() {
        int lastId = 0;

        try(
                Connection con = db.connect();
                PreparedStatement pstmt = con.prepareStatement("SELECT * FROM queues ORDER BY queue_id DESC LIMIT 1")
        ) {
            ResultSet rs = pstmt.executeQuery();
            lastId = rs.getInt("queue_id");
        }catch (SQLException e){
            e.printStackTrace();
        }

        return lastId;
    }

    public boolean getQueueStatus(int queue_id){
        boolean queue_status = false;

        try(
                Connection con = db.connect();
                PreparedStatement pstmt = con.prepareStatement("SELECT (queue_status) FROM queues WHERE queue_id = ?")
        ) {
            pstmt.setInt(1, queue_id);
            ResultSet rs = pstmt.executeQuery();
            queue_status = rs.getBoolean("queue_status");
        }catch (SQLException e){
            e.printStackTrace();
        }

        return queue_status;
    }

    public void setQueueStatus(int queue_id, boolean status){
        String query = "UPDATE queues SET queue_status = ? WHERE queue_id = ?";

        try(
                Connection con = db.connect();
                PreparedStatement pstmt = con.prepareStatement(query)
        ) {
            pstmt.setBoolean(1, status);
            pstmt.setInt(2, queue_id);
            pstmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

}
