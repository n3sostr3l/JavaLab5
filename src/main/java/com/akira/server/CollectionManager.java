package com.akira.server;

import java.util.Date;
import java.util.Hashtable;
import com.akira.general.datas.LabWork;

/**
 * Менеджер коллекции лабораторных работ.
 * <p>
 * Обеспечивает выполнение операций над коллекцией с использованием Stream API.
 * </p>
 */
public class CollectionManager {
    /** Статическое хранилище коллекции лабораторных работ. Использует Hashtable для базовой потокобезопасности. */
    private static Hashtable<Integer, LabWork> labworks = FileEditor.getCollection();
    
    /** Дата и время инициализации коллекции. Используется для команды 'info'. */
    private static Date collectionCreationTime;

    static {
        try {
            collectionCreationTime = FileEditor.getCollectionCreationTime();
        } catch (Exception e) {
            collectionCreationTime = null;
        }
        if (collectionCreationTime == null) collectionCreationTime = new Date();
    }

    /**
     * Обновляет существующий элемент коллекции по его ID.
     * При обновлении сохраняются оригинальный ID и дата создания.
     * 
     * @param id уникальный идентификатор (ключ в коллекции)
     * @param lab новый объект LabWork с обновленными данными
     * @return {@code true}, если объект был найден и успешно обновлен, иначе {@code false}
     */
    public static boolean update(Integer id, LabWork lab){
        if (labworks.containsKey(id)){
            lab.setId(labworks.get(id).getId());
            lab.setCreationDate(labworks.get(id).getCreationDate());
            labworks.put(id, lab);
            FileEditor.saveBackup(labworks);
            return true;
        }
        return false;
    }

    public static void clear() {
        labworks.clear();
        FileEditor.saveBackup(labworks);
    }

    public static boolean insert(Integer key, LabWork lab) {
        if (labworks.containsKey(key)) return false;
        
        if (lab.getId() == null || lab.getId() <= 0) {
            lab.setId(generateNextId());
        }
        if (lab.getCreationDate() == null) {
            lab.setCreationDate(new Date());
        }
        
        labworks.put(key, lab);
        FileEditor.saveBackup(labworks);
        return true;
    }

    public static void removeByKey(Integer key){
        labworks.remove(key);
        FileEditor.saveBackup(labworks);
    }

    public static boolean save() {
        boolean success = FileEditor.saveCollection(labworks);
        if (success) {
            FileEditor.deleteBackup();
        }
        return success;
    }

    public static int removeLowerKeys(Integer key) {
        java.util.List<Integer> keysToRemove = labworks.keySet().stream()
                .filter(k -> k < key)
                .collect(java.util.stream.Collectors.toList());
        keysToRemove.forEach(labworks::remove);
        if (!keysToRemove.isEmpty()) {
            FileEditor.saveBackup(labworks);
        }
        return keysToRemove.size();
    }

    public static void reload() {
        labworks = FileEditor.getCollection();
    }

    public static void restoreFromBackup() {
        if (FileEditor.hasBackup()) {
            labworks = FileEditor.getBackupCollection();
        }
    }

    public static Hashtable<Integer, LabWork> getCollection(){
        return labworks;
    }

    public static Date getCollectionCreationTime(){
        return collectionCreationTime;
    }

    private static Long generateNextId() {
        return labworks.values().stream()
                .mapToLong(LabWork::getId)
                .max()
                .orElse(0L) + 1;
    }
}
