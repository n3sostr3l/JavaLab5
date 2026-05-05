package com.akira.server.managers.configs;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostgresConfig {
    private static HikariDataSource dataSource;
    private static final Logger logger = LogManager.getLogger(PostgresConfig.class);


    public static synchronized HikariDataSource getConnection() throws SQLException{
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
    public static HikariDataSource getDataSource(){
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
