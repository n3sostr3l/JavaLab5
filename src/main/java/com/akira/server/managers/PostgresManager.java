package com.akira.server.managers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import com.akira.server.managers.configs.PostgresConfig;
import com.akira.general.datas.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostgresManager {
    // Инициализируем DataSource один раз
    private final DataSource dataSource = PostgresConfig.getDataSource();
    private static final Logger logger = LogManager.getLogger(PostgresManager.class);
    
    // SQL запросы
    private final String sql_labworks = "CREATE TABLE IF NOT EXISTS lab_works (id SERIAL PRIMARY KEY, user_login TEXT, name TEXT, coordinates_x INT, coordinates_y BIGINT NOT NULL, creation_date TIMESTAMP NOT NULL, minimal_point FLOAT4 NOT NULL, maximum_point BIGINT NOT NULL, difficulty TEXT, person_name TEXT, person_birthday TIMESTAMP, person_location_x INT, person_location_y FLOAT4, person_location_z FLOAT8)";
    private final String sql_users = "CREATE TABLE IF NOT EXISTS users (user_login TEXT PRIMARY KEY, user_password TEXT NOT NULL)";
    
    private final String sql_register_user = "INSERT INTO users (user_login, user_password) VALUES (?, ?)";
    private final String sql_login_user = "SELECT user_login FROM users WHERE user_login = ? AND user_password = ?";
    
    private final String sql_add_labwork = "INSERT INTO lab_works (user_login, name, coordinates_x, coordinates_y, creation_date, minimal_point, maximum_point, difficulty, person_name, person_birthday, person_location_x, person_location_y, person_location_z) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String sql_update_labwork = "UPDATE lab_works SET name = ?, coordinates_x = ?, coordinates_y = ?, creation_date = ?, minimal_point = ?, maximum_point = ?, difficulty = ?, person_name = ?, person_birthday = ?, person_location_x = ?, person_location_y = ?, person_location_z = ? WHERE user_login = ? AND id = ?";
    
    private final String sql_delete_labwork = "DELETE FROM lab_works WHERE user_login = ? AND id = ?";
    private final String sql_clear_labworks = "DELETE FROM lab_works WHERE user_login = ?";
    private final String sql_get_labworks = "SELECT * FROM lab_works";

    public boolean createTables() {
        try (
            Connection connect = dataSource.getConnection();
            Statement sm = connect.createStatement()
        ) {
            sm.execute(sql_labworks);
            sm.execute(sql_users);
            return true;
        } catch (SQLException e) {
            logger.error("Ошибка создания таблиц: " + e.getMessage());
            return false;
        }
    }

    public List<LabWork> getLabWorks() {
        List<LabWork> labWorks = new ArrayList<>();
        try (
            Connection connect = dataSource.getConnection();
            Statement sm = connect.createStatement();
            ResultSet rs = sm.executeQuery(sql_get_labworks)
        ) {
            while (rs.next()) {
                LabWork labWork = new LabWork();
                
                labWork.setId((long) rs.getInt("id"));
                labWork.setName(rs.getString("name"));
                
                Coordinates coordinates = new Coordinates();
                coordinates.setX(rs.getInt("coordinates_x"));
                coordinates.setY(rs.getLong("coordinates_y"));
                labWork.setCoordinates(coordinates);
                
                // creationDate имеет тип java.util.Date
                Timestamp creationTs = rs.getTimestamp("creation_date");
                if (creationTs != null) {
                    labWork.setCreationDate(new java.util.Date(creationTs.getTime()));
                }
                
                labWork.setMinimalPoint(rs.getFloat("minimal_point"));
                labWork.setMaximumPoint(rs.getLong("maximum_point"));
                
                String diffStr = rs.getString("difficulty");
                if (diffStr != null) {
                    labWork.setDifficulty(Difficulty.valueOf(diffStr));
                }

                Person person = new Person();
                person.setName(rs.getString("person_name"));
                
                // birthday имеет тип java.time.LocalDate
                Timestamp birthdayTs = rs.getTimestamp("person_birthday");
                if (birthdayTs != null) {
                    person.setBirthday(birthdayTs.toLocalDateTime().toLocalDate());
                }
                
                Location location = new Location();
                location.setX(rs.getInt("person_location_x"));
                location.setY(rs.getFloat("person_location_y"));
                location.setZ(rs.getDouble("person_location_z"));
                person.setLocation(location);
                
                labWork.setPerson(person);
                labWorks.add(labWork);
            }
            return labWorks;
        } catch (SQLException e) {
            logger.error("Ошибка получения лабораторных: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean registerUser(String user_login, String user_password) {
        try (
            Connection connect = dataSource.getConnection();
            PreparedStatement psm = connect.prepareStatement(sql_register_user)
        ) {
            psm.setString(1, user_login);
            psm.setString(2, user_password);
            psm.executeUpdate(); 
            return true;
        } catch (SQLException e) {
            // Код состояния 23505 в PostgreSQL означает нарушение уникальности
            if ("23505".equals(e.getSQLState())) {
                logger.error("Пользователь с логином {} уже существует.", user_login);
            } else {
                logger.error("Ошибка регистрации пользователя: " + e.getMessage());
            }
            return false;
        }   
    }

    public boolean loginUser(String user_login, String user_password) {
        try (
            Connection connect = dataSource.getConnection();
            PreparedStatement psm = connect.prepareStatement(sql_login_user)
        ) {
            psm.setString(1, user_login);
            psm.setString(2, user_password);
            try (ResultSet rs = psm.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Ошибка авторизации: " + e.getMessage());
            return false;
        }
    }

    public boolean addLabWork(String user_login, LabWork labWork) {
        try (
            Connection connect = dataSource.getConnection();
            PreparedStatement psm = connect.prepareStatement(sql_add_labwork)
        ) {
            psm.setString(1, user_login);
            psm.setString(2, labWork.getName());
            
            psm.setInt(3, labWork.getCoordinates().getX());
            psm.setLong(4, labWork.getCoordinates().getY());
            
            // Преобразование java.util.Date в Timestamp
            psm.setTimestamp(5, new Timestamp(labWork.getCreationDate().getTime()));
            psm.setFloat(6, labWork.getMinimalPoint());
            psm.setLong(7, labWork.getMaximumPoint());
            
            psm.setString(8, labWork.getDifficulty() != null ? labWork.getDifficulty().name() : null);
            
            psm.setString(9, labWork.getAuthor().getName());
            
            // Преобразование java.time.LocalDate в Timestamp
            if (labWork.getAuthor().getBirthday() != null) {
                psm.setTimestamp(10, Timestamp.valueOf(labWork.getAuthor().getBirthday().atStartOfDay()));
            } else {
                psm.setNull(10, java.sql.Types.TIMESTAMP);
            }
            
            psm.setInt(11, labWork.getAuthor().getLocation().getX());
            psm.setFloat(12, labWork.getAuthor().getLocation().getY());
            psm.setDouble(13, labWork.getAuthor().getLocation().getZ());

            psm.executeUpdate(); 
            return true;
        } catch (SQLException e) {
            logger.error("Ошибка добавления лабораторной: " + e.getMessage());
            return false;
        }
    }

    public boolean updateLabWork(String user_login, LabWork labWork) {
        try (
            Connection connect = dataSource.getConnection();
            PreparedStatement psm = connect.prepareStatement(sql_update_labwork)
        ) {
            psm.setString(1, labWork.getName());
            
            psm.setInt(2, labWork.getCoordinates().getX());
            psm.setLong(3, labWork.getCoordinates().getY());
            
            // Преобразование java.util.Date в Timestamp
            psm.setTimestamp(4, new Timestamp(labWork.getCreationDate().getTime()));
            psm.setFloat(5, labWork.getMinimalPoint());
            psm.setLong(6, labWork.getMaximumPoint());
            
            psm.setString(7, labWork.getDifficulty() != null ? labWork.getDifficulty().name() : null);
            
            psm.setString(8, labWork.getAuthor().getName());
            
            // Преобразование java.time.LocalDate в Timestamp
            if (labWork.getAuthor().getBirthday() != null) {
                psm.setTimestamp(9, Timestamp.valueOf(labWork.getAuthor().getBirthday().atStartOfDay()));
            } else {
                psm.setNull(9, java.sql.Types.TIMESTAMP);
            }
            
            psm.setInt(10, labWork.getAuthor().getLocation().getX());
            psm.setFloat(11, labWork.getAuthor().getLocation().getY());
            psm.setDouble(12, labWork.getAuthor().getLocation().getZ());
            
            psm.setString(13, user_login);
            psm.setLong(14, labWork.getId());

            int rowsAffected = psm.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Ошибка обновления лабораторной: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteLabWork(String user_login, long lab_work_id) {
        try (
            Connection connect = dataSource.getConnection();
            PreparedStatement psm = connect.prepareStatement(sql_delete_labwork)
        ) {
            psm.setString(1, user_login);
            psm.setLong(2, lab_work_id);
            int rowsAffected = psm.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления лабораторной: " + e.getMessage());
            return false;
        }
    }

    public boolean clearLabWorks(String user_login) {
        try (
            Connection connect = dataSource.getConnection();
            PreparedStatement psm = connect.prepareStatement(sql_clear_labworks)
        ) {
            psm.setString(1, user_login);
            psm.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("Ошибка очистки лабораторных: " + e.getMessage());
            return false;
        }
    }
}