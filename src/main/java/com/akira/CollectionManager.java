package com.akira;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;

public class CollectionManager {
    private static Hashtable<Integer, LabWork> labworks = new Hashtable<Integer, LabWork>();
    private static Date collectionCreationTime = null;

    public static Hashtable<Integer, LabWork> add(LabWork lab) {
        labworks.put(Integer.valueOf(labworks.size()), lab);
        try{
            collectionCreationTime = FileEditor.getCollectionCreationTime();
        }catch(Exception e){

        }
        if(collectionCreationTime == null) collectionCreationTime = new Date();

        return labworks;
    }

    public static Hashtable<Integer, LabWork> clear() {
        labworks.clear();
        return labworks;
    }

    public static Hashtable<Integer, LabWork> getCollection(){
        return labworks;
    }

    public static Date getCollectionCreationTime(){
        return collectionCreationTime;
    }

}