package com.swaruph.RookTownBot.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    public Connection connect() {
        String url = ConfigLoader.getInstance().getProperty("DATABASE.URL");
        Connection con = null;
        try{
            con = DriverManager.getConnection(url);
        }catch (SQLException e){
            logger.error(e.getMessage());
        }
        return con;
    }

    public void close(Connection con){
        try{
            if(con != null){
                con.close();
            }
        }catch (SQLException e){
            logger.error(e.getMessage());
        }
    }

    public void execute(String query){
        Connection con = connect();
        try{
            con.createStatement().execute(query);
        }catch (SQLException e){
            logger.error(e.getMessage());
        }finally {
            close(con);
        }
    }
}
