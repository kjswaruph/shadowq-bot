package com.swaruph.RookTownBot.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    public Connection connect() {
        String url = ConfigLoader.getInstance().getProperty("DB.URL");
        Connection con;
        try{
            con = DriverManager.getConnection(url);
        }catch (SQLException e){
            logger.error("Failed to connect to the database", e);
            throw new RuntimeException("Failed to connect to the database", e);
        }
        return con;
    }

    public void close(Connection con){
        try{
            if(con != null){
                con.close();
            }
        }catch (SQLException e){
            logger.error("Failed to close the connection", e);
        }
    }

    public void execute(String query){
        Connection con = connect();
        try{
            con.createStatement().execute(query);
        }catch (SQLException e){
            logger.error("Failed to execute the query", e);
        }finally {
            close(con);
        }
    }
}
