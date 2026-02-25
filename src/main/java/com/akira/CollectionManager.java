package com.akira;


import java.util.Date;
import java.util.Hashtable;
import java.util.UUID;
import com.akira.LabWork;

public class CollectionManager {
    private static Hashtable<String, LabWork> labworks = new Hashtable<String, LabWork>();
    private static Date collectionCreationTime = null;

    public static Hashtable<String, LabWork> add(LabWork lab) {
        UUID id = UUID.randomUUID();
        String s = id.toString();
        labworks.put(s, lab);
        try{
            collectionCreationTime = FileEditor.getCollectionCreationTime();
        }catch(Exception e){
            System.err.println("");
        }
        if(collectionCreationTime == null) collectionCreationTime = new Date();

        return labworks;
    }
    public static boolean update(String id, LabWork lab){
        if (labworks.containsKey(id)){
            labworks.put(id, lab);
            return true;
        }
        else {
            return false;
        }
    }

    public static Hashtable<String, LabWork> clear() {
        labworks.clear();
        return labworks;
    }

    public static Hashtable<String, LabWork> getCollection(){
        return labworks;
    }

    public static Date getCollectionCreationTime(){
        return collectionCreationTime;
    }

}