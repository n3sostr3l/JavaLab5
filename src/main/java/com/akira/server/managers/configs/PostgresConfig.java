package com.akira.server.managers.configs;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class PostgresConfig {
    private static HikariDataSource dataSource;


    public static synchronized DataSource getConnection() throws SQLException{
        if (dataSource == null){
            Dotenv dotenv = Dotenv.load();
            String POSTGRES_URL = dotenv.get("POSTGRES_URL");
            String POSTGRES_USER = dotenv.get("POSTGRES_USER");
            String POSTGRES_PASSWORD = dotenv.get("POSTGRES_PASSWORD");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(POSTGRES_URL);
            config.setUsername(POSTGRES_USER);
            config.setPassword(POSTGRES_PASSWORD);
            config.setMaximumPoolSize(10);
            
            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }
    public static DataSource getDataSource(){
        try{
            if (dataSource == null){
                getConnection();
            }
        }
        catch (SQLException e){
            
        }
        return dataSource;
    }
}
