package com.akira.server;

import java.util.Date;
import java.util.Hashtable;
import com.akira.general.datas.LabWork;

/**
 * Класс для управления коллекцией лабораторных работ.
 */
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
            lab.setCreationDate(labworks.get(id).getCreationDate());
            labworks.put(id, lab);
            return true;
        }
        return false;
    }

    public static void clear() {
        labworks.clear();
    }

    public static Hashtable<Integer, LabWork> getCollection(){
        return labworks;
    }

    public static Date getCollectionCreationTime(){
        return collectionCreationTime;
    }

    public static boolean insert(Integer key, LabWork lab) {
        if (labworks.containsKey(key)) return false;
        
        // Автоматическая генерация полей (ТЗ ЛР6)
        if (lab.getId() == null || lab.getId() <= 0) {
            lab.setId(generateNextId());
        }
        if (lab.getCreationDate() == null) {
            lab.setCreationDate(new Date());
        }
        
        labworks.put(key, lab);
        return true;
    }

    private static Long generateNextId() {
        return labworks.values().stream()
                .mapToLong(LabWork::getId)
                .max()
                .orElse(0L) + 1;
    }

    public static void removeByKey(Integer key){
        labworks.remove(key);
    }

    public static boolean save() {
        return FileEditor.save(labworks);
    }

    public static int removeLowerKeys(Integer key) {
        java.util.List<Integer> keysToRemove = labworks.keySet().stream()
                .filter(k -> k < key)
                .collect(java.util.stream.Collectors.toList());
        keysToRemove.forEach(labworks::remove);
        return keysToRemove.size();
    }
}
