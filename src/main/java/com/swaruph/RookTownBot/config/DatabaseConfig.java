package com.swaruph.RookTownBot.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConfig {

    public Connection connect() {
        String url = ConfigLoader.getProperty("DB.URL");
        Connection con = null;
        try{
            con = DriverManager.getConnection(url);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return con;
    }

    public static void close(Connection con){
        try{
            if(con != null){
                con.close();
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void execute(String query){
        Connection con = connect();
        try{
            con.createStatement().execute(query);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            close(con);
        }
    }
}
