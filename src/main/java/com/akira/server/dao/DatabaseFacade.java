package com.akira.server.dao;

import com.akira.general.datas.LabWork;
import java.util.Hashtable;

public class DatabaseFacade {
    private static DatabaseFacade instance;
    private final PostgresDao dao;

    private DatabaseFacade(){
        dao = new PostgresDao();
    }

    public static synchronized DatabaseFacade getInstance(){
        if (instance == null) instance = new DatabaseFacade();
        return instance;
    }

    public boolean registerUser(String login, String passwordHash){
        return dao.registerUser(login, passwordHash);
    }

    public boolean loginUser(String login, String passwordHash){
        return dao.loginUser(login, passwordHash);
    }

    public boolean addLabWork(String userLogin, LabWork labWork, Integer key){
        return dao.addLabWork(userLogin, labWork, key);
    }

    public boolean updateLabWork(String userLogin, LabWork labWork, Integer key){
        return dao.updateLabWork(userLogin, labWork, key);
    }

    public boolean deleteLabWork(String userLogin, Integer key){
        return dao.deleteLabWork(userLogin, key);
    }

    public boolean clearLabWorks(String userLogin){
        return dao.clearLabWorks(userLogin);
    }

    public Hashtable<Integer, LabWork> getLabWorks(){
        return dao.getLabWorks();
    }

    public java.util.Date getCollectionCreationTime(){
        return dao.getCollectionCreationTime();
    }
}
