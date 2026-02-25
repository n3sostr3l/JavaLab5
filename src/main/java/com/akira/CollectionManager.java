package com.akira;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;

public class CollectionManager {
    static Hashtable<Integer, LabWork> labworks = new Hashtable<Integer, LabWork>();
    static Date collectionCreationTime = null;

    public static Hashtable<Integer, LabWork> add(LabWork lab) {
        labworks.put(Integer.valueOf(labworks.size()), lab);
        if(collectionCreationTime == null) collectionCreationTime = new Date();
        return labworks;
    }

    public static Hashtable<Integer, LabWork> clear() {
        labworks.clear();
        return labworks;
    }

    public static Object getCollection(){
        return labworks;
    }

}