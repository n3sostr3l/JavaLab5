package com.akira.server.managers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Hashtable;
import javax.sql.DataSource;

import com.akira.server.managers.configs.PostgresConfig;
import com.akira.general.datas.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostgresManager {
    public static PostgresManager instance;

    public static PostgresManager getInstance() {
        instance = instance == null ? new PostgresManager() : instance;
        return instance;
    }

    private final DataSource dataSource = PostgresConfig.getDataSource();
    private final Logger logger = LogManager.getLogger(PostgresManager.class);

    // SQL запросы для таблиц
    private final String sql_users = "CREATE TABLE IF NOT EXISTS users (user_login TEXT PRIMARY KEY, user_password TEXT NOT NULL)";
    
    private final String sql_sequence = "CREATE SEQUENCE IF NOT EXISTS labwork_id_seq START 1 INCREMENT 1";
    
        private final String sql_labworks = "CREATE TABLE IF NOT EXISTS labworks (" +
            "id BIGINT PRIMARY KEY DEFAULT nextval('labwork_id_seq'), " +
            "lab_key INTEGER NOT NULL UNIQUE, " +
            "name VARCHAR(255) NOT NULL CHECK (name <> ''), " +
            "coord_x INTEGER NOT NULL CHECK (coord_x > -881), " +
            "coord_y BIGINT NOT NULL, " +
            "creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "minimal_point FLOAT4 CHECK (minimal_point > 0), " +
            "maximum_point BIGINT NOT NULL CHECK (maximum_point > 0), " +
            "description TEXT, " +
            "difficulty VARCHAR(50), " +
            "author_name VARCHAR(255) NOT NULL CHECK (author_name <> ''), " +
            "author_birthday TIMESTAMP, " +
            "loc_x INTEGER, " +
            "loc_y FLOAT4, " +
            "loc_z DOUBLE PRECISION, " +
            "owner_login TEXT NOT NULL, " +
            "FOREIGN KEY (owner_login) REFERENCES users(user_login) ON DELETE CASCADE" +
            ")";
    
    // SQL для метаданных (время создания коллекции)
    private final String sql_metadata = "CREATE TABLE IF NOT EXISTS collection_metadata (creation_time TIMESTAMP NOT NULL)";
    private final String sql_set_creation_time = "INSERT INTO collection_metadata (creation_time) SELECT CURRENT_TIMESTAMP WHERE NOT EXISTS (SELECT 1 FROM collection_metadata)";
    private final String sql_get_creation_time = "SELECT creation_time FROM collection_metadata LIMIT 1";

    // SQL операции
    private final String sql_register_user = "INSERT INTO users (user_login, user_password) VALUES (?, ?)";
    private final String sql_login_user = "SELECT user_login FROM users WHERE user_login = ? AND user_password = ?";
    private final String sql_add_labwork = "INSERT INTO labworks (lab_key, name, coord_x, coord_y, creation_date, minimal_point, maximum_point, description, difficulty, author_name, author_birthday, loc_x, loc_y, loc_z, owner_login) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
    private final String sql_update_labwork = "UPDATE labworks SET name=?, coord_x=?, coord_y=?, minimal_point=?, maximum_point=?, description=?, difficulty=?, author_name=?, author_birthday=?, loc_x=?, loc_y=?, loc_z=? WHERE id=? AND owner_login=?";
    private final String sql_delete_labwork = "DELETE FROM labworks WHERE lab_key=? AND owner_login=?";
    private final String sql_clear_labworks = "DELETE FROM labworks WHERE owner_login=?";
    private static final String sql_get_labworks = "SELECT * FROM labworks";
    private final String sql_check_labwork_id = "SELECT * FROM labworks WHERE owner_login=? AND id=?";
    private final String sql_check_labwork_key = "SELECT * FROM labworks WHERE owner_login=? AND key=?";
    private final String sql_reset_password = "UPDATE users SET user_login=?, user_password=?";

    // public boolean checkLabwork(String user_login, boolean isId, key){
    //     try (
    //         Connection connect = dataSource.getConnection();
    //         if (isId){
    //             PreparedStatement psm = connect.prepareStatement(sql_check_labwork)
    //         }
    //         PreparedStatement psm = connect.prepareStatement(sql_check_labwork)
    //     ) {
    //         psm.setString(1, user_login);
    //         int rows_cnt = psm.executeUpdate();
    //         return rows_cnt > 0;
    //     } catch (SQLException e) {
    //         logger.error("Ошибка с работой базы данных: " + e.getMessage());
    //         return false;
    //     }
    // }
    public boolean resetPassword(String user_login, String user_password){
        try (
            Connection connect = dataSource.getConnection();
            PreparedStatement psm = connect.prepareStatement(sql_reset_password)
        ) {
            psm.setString(1, user_login);
            psm.setString(2, user_password);
            int rows_cnt = psm.executeUpdate();
            return rows_cnt > 0;
        } catch (SQLException e) {
            logger.error("Ошибка регистрации пользователя: " + e.getMessage());
            return false;
        }
    }
    public boolean createTables() {
        try (
            Connection connect = dataSource.getConnection();
            Statement sm = connect.createStatement()
        ) {
            sm.execute(sql_sequence);
            sm.execute(sql_users);
            sm.execute(sql_labworks);
            sm.execute(sql_metadata);
            sm.execute(sql_set_creation_time);
            return true;
        } catch (SQLException e) {
            logger.error("Ошибка создания таблиц: " + e.getMessage());
            return false;
        }
    }

    /**
     * Возвращает время создания коллекции из таблицы метаданных.
     */
    public java.util.Date getCollectionCreationTime() {
        try (
            Connection connect = dataSource.getConnection();
            Statement sm = connect.createStatement();
            ResultSet rs = sm.executeQuery(sql_get_creation_time)
        ) {
            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("creation_time");
                return new java.util.Date(ts.getTime());
            }
        } catch (SQLException e) {
            logger.error("Ошибка получения времени создания коллекции: " + e.getMessage());
        }
        return null;
    }

    public Hashtable<Integer, LabWork> getLabWorks() {
        Hashtable<Integer, LabWork> labWorks = new Hashtable<>();
        try (
            Connection connect = dataSource.getConnection();
            Statement sm = connect.createStatement();
            ResultSet rs = sm.executeQuery(sql_get_labworks)
        ) {
            while (rs.next()) {
                int labKey = rs.getInt("lab_key");
                boolean labKeyWasNull = rs.wasNull();
                Long id = rs.getLong("id");
                LabWork labWork = new LabWork();
                labWork.setId(id);
                labWork.setName(rs.getString("name"));

                Coordinates coordinates = new Coordinates();
                coordinates.setX(rs.getInt("coord_x"));
                coordinates.setY(rs.getLong("coord_y"));
                labWork.setCoordinates(coordinates);

                Timestamp creationTs = rs.getTimestamp("creation_date");
                if (creationTs != null) {
                    labWork.setCreationDate(new java.util.Date(creationTs.getTime()));
                }

                float minPoint = rs.getFloat("minimal_point");
                if (rs.wasNull()) {
                    labWork.setMinimalPoint(null);
                } else {
                    labWork.setMinimalPoint(minPoint);
                }
                labWork.setMaximumPoint(rs.getLong("maximum_point"));
                labWork.setDescription(rs.getString("description"));

                String diffStr = rs.getString("difficulty");
                if (diffStr != null) {
                    labWork.setDifficulty(Difficulty.valueOf(diffStr));
                }

                Person person = new Person();
                person.setName(rs.getString("author_name"));

                Timestamp birthdayTs = rs.getTimestamp("author_birthday");
                if (birthdayTs != null) {
                    person.setBirthday(birthdayTs.toLocalDateTime().toLocalDate());
                }

                Location location = new Location();
                Integer locX = rs.getInt("loc_x");
                location.setX(locX == 0 && rs.wasNull() ? 0 : locX);
                Float locY = rs.getFloat("loc_y");
                location.setY(locY == 0 && rs.wasNull() ? 0 : locY);
                Double locZ = rs.getDouble("loc_z");
                location.setZ(locZ == 0 && rs.wasNull() ? 0 : locZ);
                person.setLocation(location);

                labWork.setPerson(person);
                int mapKey = labKeyWasNull ? Math.toIntExact(id) : labKey;
                labWorks.put(mapKey, labWork);
            }
            return labWorks;
        } catch (SQLException e) {
            logger.error("Ошибка получения лабораторных: " + e.getMessage());
            return labWorks;
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

    public boolean addLabWork(String user_login, LabWork labWork, Integer key) {
        try (
            Connection connect = dataSource.getConnection();
            PreparedStatement psm = connect.prepareStatement(sql_add_labwork, Statement.RETURN_GENERATED_KEYS)
        ) {
            psm.setInt(1, key);
            psm.setString(2, labWork.getName());
            psm.setInt(3, labWork.getCoordinates().getX());
            psm.setLong(4, labWork.getCoordinates().getY());
            psm.setTimestamp(5, new Timestamp(labWork.getCreationDate().getTime()));
            Float minPoint = labWork.getMinimalPoint();
            if (minPoint != null && minPoint > 0) {
                psm.setFloat(6, minPoint);
            } else {
                psm.setNull(6, java.sql.Types.FLOAT);
            }
            psm.setLong(7, labWork.getMaximumPoint());
            psm.setString(8, labWork.getDescription());
            psm.setString(9, labWork.getDifficulty() != null ? labWork.getDifficulty().name() : null);
            psm.setString(10, labWork.getAuthor().getName());

            if (labWork.getAuthor().getBirthday() != null) {
                psm.setTimestamp(11, Timestamp.valueOf(labWork.getAuthor().getBirthday().atStartOfDay()));
            } else {
                psm.setNull(11, java.sql.Types.TIMESTAMP);
            }

            Location location = labWork.getAuthor().getLocation();
            if (location != null) {
                psm.setInt(12, location.getX());
                psm.setFloat(13, location.getY());
                psm.setDouble(14, location.getZ());
            } else {
                psm.setNull(12, java.sql.Types.INTEGER);
                psm.setNull(13, java.sql.Types.FLOAT);
                psm.setNull(14, java.sql.Types.DOUBLE);
            }
            psm.setString(15, user_login);

            boolean hasResultSet = psm.execute();
            if (hasResultSet) {
                try (ResultSet rs = psm.getResultSet()) {
                    if (rs != null && rs.next()) {
                        labWork.setId(rs.getLong(1));
                        return true;
                    }
                }
                return false;
            }
            int rows = psm.getUpdateCount();
            if (rows > 0) {
                try (ResultSet keys = psm.getGeneratedKeys()) {
                    if (keys != null && keys.next()) {
                        labWork.setId(keys.getLong(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.error("Ошибка добавления лабораторной: " + e.getMessage());
            return false;
        }
    }
    public boolean updateLabWork(String user_login, LabWork labWork, Integer key) {
        try (
            Connection connect = dataSource.getConnection();
            PreparedStatement psm = connect.prepareStatement(sql_update_labwork)
        ) {
            psm.setString(1, labWork.getName());
            psm.setInt(2, labWork.getCoordinates().getX());
            psm.setLong(3, labWork.getCoordinates().getY());
            Float minPoint = labWork.getMinimalPoint();
            if (minPoint != null && minPoint > 0) {
                psm.setFloat(4, minPoint);
            } else {
                psm.setNull(4, java.sql.Types.FLOAT);
            }
            psm.setLong(5, labWork.getMaximumPoint());
            psm.setString(6, labWork.getDescription());
            psm.setString(7, labWork.getDifficulty() != null ? labWork.getDifficulty().name() : null);
            psm.setString(8, labWork.getAuthor().getName());

            if (labWork.getAuthor().getBirthday() != null) {
                psm.setTimestamp(9, Timestamp.valueOf(labWork.getAuthor().getBirthday().atStartOfDay()));
            } else {
                psm.setNull(9, java.sql.Types.TIMESTAMP);
            }

            Location location = labWork.getAuthor().getLocation();
            if (location != null) {
                psm.setInt(10, location.getX());
                psm.setFloat(11, location.getY());
                psm.setDouble(12, location.getZ());
            } else {
                psm.setNull(10, java.sql.Types.INTEGER);
                psm.setNull(11, java.sql.Types.FLOAT);
                psm.setNull(12, java.sql.Types.DOUBLE);
            }
            psm.setLong(13, labWork.getId());
            psm.setString(14, user_login);

            int rowsAffected = psm.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Ошибка обновления лабораторной: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteLabWork(String user_login, Integer key) {
        try (
            Connection connect = dataSource.getConnection();
            PreparedStatement psm = connect.prepareStatement(sql_delete_labwork)
        ) {
            psm.setInt(1, key);
            psm.setString(2, user_login);
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
            int res = psm.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            logger.error("Ошибка очистки лабораторных: " + e.getMessage());
            return false;
        }
    }
}