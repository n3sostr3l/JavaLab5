package com.akira.server.managers;

import java.util.Date;
import java.util.Hashtable;
import com.akira.general.datas.LabWork;
import com.akira.server.FileEditor;

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
    /** Флаг, разрешающий сохранение при завершении работы (Shutdown Hook). */
    private static boolean saveOnExit = true;

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
     * Вставляет новый элемент в коллекцию.
     * @param key ключ элемента
     * @param lab объект лабораторной работы
     * @return true, если вставка прошла успешно
     */
    public static boolean insert(Integer key, LabWork lab) {
        lab.setId(generateNextId());

        if (lab.getCreationDate() == null) {
            lab.setCreationDate(new Date());
        }
        
        labworks.put(key, lab);
        return true;
    }

    /**
     * Удаляет элемент из коллекции по ключу.
     * @param key ключ элемента
     */
    public static void removeByKey(Integer key){
        labworks.remove(key);
    }

    /**
     * Сохраняет коллекцию в файл.
     * @return true, если сохранение прошло успешно
     */
    public static boolean save() {
        boolean success = FileEditor.saveCollection(labworks);
        if (success) {
            FileEditor.saveToFile(FileEditor.DATA_FILE_NAME, labworks);
        }
        return success;
    }

    /**
     * Удаляет все элементы, ключ которых меньше заданного.
     * @param key пороговое значение ключа
     * @return количество удаленных элементов
     */
    public static int removeLowerKeys(Integer key) {
        java.util.List<Integer> keysToRemove = labworks.keySet().stream()
                .filter(k -> k < key)
                .collect(java.util.stream.Collectors.toList());
        keysToRemove.forEach(labworks::remove);
        return keysToRemove.size();
    }

    /**
     * Перезагружает коллекцию из файла.
     */
    public static void reload() {
        labworks = FileEditor.getCollection();
    }

    /**
     * Загружает сессию из файла.
     */
    public static void loadSession() {
        String fileName = FileEditor.DATA_FILE_NAME;
        if (!FileEditor.exists(fileName)) {
            FileEditor.saveToFile(fileName, new Hashtable<>());
        }
        
        labworks = FileEditor.getSessionCollection(fileName);
        if (labworks == null) labworks = new Hashtable<>();

        FileEditor.saveToFile(FileEditor.DATA_FILE_NAME, labworks);
    }

    /**
     * Устанавливает флаг сохранения при выходе.
     * @param value значение флага
     */
    public static void setSaveOnExit(boolean value) {
        saveOnExit = value;
    }

    /**
     * Проверяет, включено ли сохранение при выходе.
     * @return true, если включено
     */
    public static boolean isSaveOnExit() {
        return saveOnExit;
    }

    /**
     * Возвращает коллекцию лабораторных работ.
     * @return хеш-таблица объектов
     */
    public static Hashtable<Integer, LabWork> getCollection(){
        return labworks;
    }

    /**
     * Возвращает время создания коллекции.
     * @return дата и время создания
     */
    public static Date getCollectionCreationTime(){
        return collectionCreationTime;
    }

    /**
     * Генерирует следующий уникальный идентификатор для объекта коллекции.
     * @return новый ID
     */
    private static Long generateNextId() {
        return labworks.values().stream()
                .mapToLong(LabWork::getId)
                .max()
                .orElse(0L) + 1;
    }
}
