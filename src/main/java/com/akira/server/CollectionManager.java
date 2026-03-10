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
            return true;
        }
        return false;
    }

    /**
     * Полностью очищает текущую коллекцию объектов.
     */
    public static void clear() {
        labworks.clear();
    }

    /**
     * Предоставляет доступ к объекту коллекции.
     * @return Hashtable, содержащий все лабораторные работы
     */
    public static Hashtable<Integer, LabWork> getCollection(){
        return labworks;
    }

    /**
     * Возвращает дату создания (инициализации) коллекции.
     * @return объект {@link Date} с временем создания
     */
    public static Date getCollectionCreationTime(){
        return collectionCreationTime;
    }

    /**
     * Вставляет новый элемент в коллекцию по заданному ключу.
     * Если у объекта отсутствует ID или дата создания, они генерируются автоматически.
     * 
     * @param key ключ для вставки
     * @param lab объект лабораторной работы
     * @return {@code true}, если вставка прошла успешно, {@code false}, если ключ уже занят
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

    /**
     * Генерирует следующий доступный уникальный ID на основе максимального ID в коллекции.
     * @return новый уникальный идентификатор типа Long
     */
    private static Long generateNextId() {
        return labworks.values().stream()
                .mapToLong(LabWork::getId)
                .max()
                .orElse(0L) + 1;
    }

    /**
     * Удаляет объект из коллекции по его ключу.
     * @param key ключ удаляемого объекта
     */
    public static void removeByKey(Integer key){
        labworks.remove(key);
    }

    /**
     * Сохраняет текущее состояние коллекции в файл через {@link FileEditor}.
     * @return {@code true}, если сохранение прошло успешно
     */
    public static boolean save() {
        return FileEditor.saveCollection(labworks);
    }

    /**
     * Удаляет все элементы коллекции, ключи которых меньше заданного значения.
     * 
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
}
