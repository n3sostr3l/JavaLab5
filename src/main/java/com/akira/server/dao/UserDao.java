package com.akira.server.dao;

public interface UserDao {
    boolean registerUser(String login, String passwordHash);
    boolean loginUser(String login, String passwordHash);
}
