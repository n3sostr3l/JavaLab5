package com.akira;


import java.util.Date;
import java.util.Hashtable;

public class CollectionManager {
    private static Hashtable<Integer, LabWork> labworks = FileEditor.getCollection();
    private static Date collectionCreationTime;

    static {
        try {
            collectionCreationTime = FileEditor.getCollectionCreationTime();
        } catch (Exception e) {
            collectionCreationTime = null;
        }
        if (collectionCreationTime == null) collectionCreationTime = new Date();
    }
    public static boolean update(Integer id, LabWork lab){
        if (labworks.containsKey(id)){
            lab.setId(labworks.get(id).getId());
            labworks.put(id, lab);
            return true;
        }
        else {
            return false;
        }
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
                newLab.setId(entry.getValue().getId());
                newLab.setCreationDate(entry.getValue().getCreationDate());
                labworks.put(entry.getKey(), newLab);
                return true;
            }
        }
        return false;
    }

    public static void insert(Integer key, LabWork lab) {
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
    public static void removeByKey(Integer key){
        labworks.remove(key);
    }

    public static int removeLowerKeys(Integer key) {
        var keysToRemove = new java.util.ArrayList<Integer>();
        for (Integer k : labworks.keySet()) {
            if (k < key) {
                keysToRemove.add(k);
            }
        }
        for (Integer k : keysToRemove) {
            labworks.remove(k);
        }
        return keysToRemove.size();
    }

}