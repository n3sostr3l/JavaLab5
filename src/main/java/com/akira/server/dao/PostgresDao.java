package com.akira.server.dao;

import com.akira.server.managers.PostgresManager;
import com.akira.general.datas.LabWork;
import java.util.Hashtable;

public class PostgresDao implements UserDao, LabWorkDao {
    private final PostgresManager pm = PostgresManager.getInstance();

    @Override
    public boolean registerUser(String login, String passwordHash) {
        return pm.registerUser(login, passwordHash);
    }

    @Override
    public boolean loginUser(String login, String passwordHash) {
        return pm.loginUser(login, passwordHash);
    }

    @Override
    public boolean addLabWork(String userLogin, LabWork labWork, Integer key) {
        return pm.addLabWork(userLogin, labWork, key);
    }

    @Override
    public boolean updateLabWork(String userLogin, LabWork labWork, Integer key) {
        return pm.updateLabWork(userLogin, labWork, key);
    }

    @Override
    public boolean deleteLabWork(String userLogin, Integer key) {
        return pm.deleteLabWork(userLogin, key);
    }

    @Override
    public boolean clearLabWorks(String userLogin) {
        return pm.clearLabWorks(userLogin);
    }

    @Override
    public String getOwnerLoginByKey(Integer key) {
        return pm.getOwnerLoginByKey(key);
    }

    @Override
    public String getOwnerLoginById(Long id) {
        return pm.getOwnerLoginById(id);
    }

    @Override
    public Hashtable<Integer, LabWork> getLabWorks() {
        return pm.getLabWorks();
    }

    @Override
    public java.util.Date getCollectionCreationTime() {
        return pm.getCollectionCreationTime();
    }
}
