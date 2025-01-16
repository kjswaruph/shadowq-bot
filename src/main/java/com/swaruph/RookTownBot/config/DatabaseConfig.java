package com.swaruph.RookTownBot.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    Properties properties = new Properties();

    public DatabaseConfig(){
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Connection connect() {

        String url = properties.getProperty("DB.URL");
        Connection con = null;
        try{
            con = DriverManager.getConnection(url);
        }catch (SQLException e){
            System.out.println(e.getMessage());
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
