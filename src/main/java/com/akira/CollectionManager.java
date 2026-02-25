package com.akira;


import java.util.Date;
import java.util.Hashtable;
import java.util.UUID;
import com.akira.LabWork;

public class CollectionManager {
    private static Hashtable<String, LabWork> labworks = new Hashtable<String, LabWork>();
    private static Date collectionCreationTime;

    static {
        try {
            collectionCreationTime = FileEditor.getCollectionCreationTime();
        } catch (Exception e) {
            collectionCreationTime = null;
        }
        if (collectionCreationTime == null) collectionCreationTime = new Date();
    }

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

    public static boolean existsById(Long labId) {
        for (var entry : labworks.entrySet()) {
            if (entry.getValue().getId() != null && entry.getValue().getId().equals(labId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean updateById(Long labId, LabWork newLab) {
        for (var entry : labworks.entrySet()) {
            if (entry.getValue().getId() != null && entry.getValue().getId().equals(labId)) {
                newLab.setCreationDate(entry.getValue().getCreationDate());
                labworks.put(entry.getKey(), newLab);
                return true;
            }
        }
        return false;
    }

    public static void insert(String key, LabWork lab) {
        labworks.put(key, lab);
        if (collectionCreationTime == null) {
            try {
                collectionCreationTime = FileEditor.getCollectionCreationTime();
            } catch (Exception e) {
                System.err.println("");
            }
            if (collectionCreationTime == null) collectionCreationTime = new Date();
        }
    }

}