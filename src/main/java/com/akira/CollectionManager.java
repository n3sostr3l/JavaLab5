package com.akira;


import java.util.Date;
import java.util.Hashtable;

/**
 * Класс для управления коллекцией лабораторных работ.
 * <p>
 * Обеспечивает основные операции CRUD над коллекцией типа {@link Hashtable},
 * где ключом является {@link Integer}, а значением — {@link LabWork}.
 * При загрузке коллекция автоматически заполняется из файла.
 * </p>
 */
public class CollectionManager {
    /** Коллекция лабораторных работ, загруженная из файла */
    private static Hashtable<Integer, LabWork> labworks = FileEditor.getCollection();
    /** Дата создания коллекции */
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
     * Обновляет лабораторную работу по ключу коллекции.
     *
     * @param id ключ элемента в коллекции
     * @param lab новое значение лабораторной работы
     * @return true, если элемент был обновлён; false, если элемент с таким ключом не найден
     */
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

    /**
     * Очищает коллекцию, удаляя все элементы.
     *
     * @return пустая коллекция
     */
    public static Hashtable<Integer, LabWork> clear() {
        labworks.clear();
        return labworks;
    }

    /**
     * Возвращает текущую коллекцию лабораторных работ.
     *
     * @return коллекция лабораторных работ
     */
    public static Hashtable<Integer, LabWork> getCollection(){
        return labworks;
    }

    /**
     * Возвращает дату создания коллекции.
     *
     * @return дата создания коллекции
     */
    public static Date getCollectionCreationTime(){
        return collectionCreationTime;
    }

    /**
     * Проверяет существование лабораторной работы по её id.
     *
     * @param labId уникальный идентификатор лабораторной работы
     * @return true, если элемент с таким id существует; false в противном случае
     */
    public static boolean existsById(Long labId) {
        for (var entry : labworks.entrySet()) {
            if (entry.getValue().getId() != null && entry.getValue().getId().equals(labId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Обновляет лабораторную работу по её id.
     * <p>
     * Сохраняет оригинальные id и дату создания элемента.
     * </p>
     *
     * @param labId уникальный идентификатор лабораторной работы
     * @param newLab новое значение лабораторной работы
     * @return true, если элемент был обновлён; false, если элемент с таким id не найден
     */
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

    /**
     * Добавляет лабораторную работу в коллекцию с указанным ключом.
     *
     * @param key ключ, под которым элемент будет храниться в коллекции
     * @param lab лабораторная работа для добавления
     */
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

    /**
     * Удаляет элемент из коллекции по ключу.
     *
     * @param key ключ элемента для удаления
     */
    public static void removeByKey(Integer key){
        labworks.remove(key);
    }

    /**
     * Удаляет из коллекции все элементы, ключ которых меньше заданного.
     *
     * @param key значение ключа для сравнения
     * @return количество удалённых элементов
     */
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