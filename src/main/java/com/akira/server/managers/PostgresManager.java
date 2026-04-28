package com.akira.server.managers;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import javax.sql.DataSource;

import com.akira.server.managers.configs.PostgresConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostgresManager {

    private static final Logger logger = LogManager.getLogger(PostgresManager.class);
    private DataSource dataSource;

    public boolean createTables(){
        String sql_labworks = "CREATE TABLE IF NOT EXISTS lab_works (id SERIAL)";
        String sql_users = "CREATE TABLE IF NOT EXISTS users (user_login VARCHAR(255) PRIMARY KEY, user_password VARCHAR(255) NOT NULL)";
        try{
            dataSource = PostgresConfig.getDataSource();
        }
        catch (SQLException e){
            logger.error("Внимание! Не удалось подключиться к базе данных: {}", e.getMessage());
            return false;
        }
        
        try(
            Connection connect = dataSource.getConnection();
            Statement sm = connect.createStatement()
        ){
            sm.execute(sql_labworks);
            sm.execute(sql_users);
            return true;
        }
        catch (SQLException e){
            System.err.println("Ошибка работы с базой данных: " + e.getMessage());
            return false;
        }
    }
    public boolean getLabWorks(String user_login){
        String sql_get_labworks = "SELECT * FROM lab_works WHERE user_login = ?";
        try{
            dataSource = PostgresConfig.getDataSource();
        }
        catch (SQLException e){
            logger.error("Внимание! Не удалось подключиться к базе данных: {}", e.getMessage());
            return false;
        }
        
        try(
            Connection connect = dataSource.getConnection();
            PreparedStatement psm = connect.prepareStatement(sql_get_labworks)
        ){
            psm.setString(1, user_login);
            return true;
        }
        catch (SQLException e){
            System.err.println("Ошибка работы с базой данных: " + e.getMessage());
            return false;
        }
    }
    public boolean registerUser(String user_login, String user_password){
    }
    public boolean loginUser(String user_login, String user_password){
    }
    public boolean addLabWork(String user_login, String lab_work){
    }
    public boolean updateLabWork(String user_login, String lab_work){
    }
    public boolean deleteLabWork(String user_login, String lab_work){
    }
    public boolean clearLabWorks(String user_login){
    }
}
