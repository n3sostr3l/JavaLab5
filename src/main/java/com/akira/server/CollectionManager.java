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

    /**
     * Обновляет элемент в коллекции.
     * @param id ключ
     * @param lab объект
     * @return true если успешно
     */
    public static boolean update(Integer id, LabWork lab){
        if (labworks.containsKey(id)){
            lab.setId(labworks.get(id).getId());
            lab.setCreationDate(labworks.get(id).getCreationDate());
            labworks.put(id, lab);
            return true;
        }
        return false;
    }

    /**
     * Очищает коллекцию.
     */
    public static void clear() {
        labworks.clear();
    }

    /**
     * Возвращает коллекцию.
     * @return Hashtable
     */
    public static Hashtable<Integer, LabWork> getCollection(){
        return labworks;
    }

    /**
     * Возвращает дату инициализации.
     * @return Date
     */
    public static Date getCollectionCreationTime(){
        return collectionCreationTime;
    }

    /**
     * Вставляет элемент.
     * @param key ключ
     * @param lab объект
     * @return true если успешно
     */
    public static boolean insert(Integer key, LabWork lab) {
        if (labworks.containsKey(key)) return false;
        
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

    /**
     * Удаляет по ключу.
     * @param key ключ
     */
    public static void removeByKey(Integer key){
        labworks.remove(key);
    }

    /**
     * Сохраняет коллекцию.
     * @return true если успешно
     */
    public static boolean save() {
        return FileEditor.saveCollection(labworks);
    }

    /**
     * Удаляет элементы с ключами ниже заданного.
     * @param key ключ
     * @return кол-во удаленных
     */
    public static int removeLowerKeys(Integer key) {
        java.util.List<Integer> keysToRemove = labworks.keySet().stream()
                .filter(k -> k < key)
                .collect(java.util.stream.Collectors.toList());
        keysToRemove.forEach(labworks::remove);
        return keysToRemove.size();
    }
}
