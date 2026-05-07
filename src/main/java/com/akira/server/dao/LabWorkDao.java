package com.akira.server.dao;

import com.akira.general.datas.LabWork;
import java.util.Hashtable;

public interface LabWorkDao {
    boolean addLabWork(String userLogin, LabWork labWork, Integer key);
    boolean updateLabWork(String userLogin, LabWork labWork, Integer key);
    boolean deleteLabWork(String userLogin, Integer key);
    boolean clearLabWorks(String userLogin);
    String getOwnerLoginByKey(Integer key);
    String getOwnerLoginById(Long id);
    Hashtable<Integer, LabWork> getLabWorks();
    java.util.Date getCollectionCreationTime();
}
