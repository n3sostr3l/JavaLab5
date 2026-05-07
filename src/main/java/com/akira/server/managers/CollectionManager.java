package com.akira.server.managers;

import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantLock;
import com.akira.general.datas.LabWork;
import com.akira.server.dao.*;

/**
 * Менеджер коллекции лабораторных работ.
 * <p>
 * Обеспечивает выполнение операций над коллекцией с использованием Stream API.
 * </p>
 */
public class CollectionManager {
    /** Статическое хранилище коллекции лабораторных работ. Использует Hashtable для базовой потокобезопасности. */
    private static Hashtable<Integer, LabWork> labworks = PostgresManager.getInstance().getLabWorks();
    private static final ReentrantLock lock = new ReentrantLock(true);
    
    /** Дата и время инициализации коллекции. Используется для команды 'info'. */
    private static Date collectionCreationTime;
    /** Флаг, разрешающий сохранение при завершении работы (Shutdown Hook). */
    private static boolean saveOnExit = true;

    static {
        try {
            collectionCreationTime = PostgresManager.getInstance().getCollectionCreationTime();
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
    public static boolean update(String user_login, Integer id, LabWork lab){
        lock.lock();
        try {
            if (!labworks.containsKey(id)) {
                return false;
            }
            LabWork existing = labworks.get(id);
            lab.setId(existing.getId());
            lab.setCreationDate(existing.getCreationDate());
            boolean isSuccess = DatabaseFacade.getInstance().updateLabWork(user_login, lab, id);
            if (isSuccess) {
                labworks.put(id, lab);
            }
            return isSuccess;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Очищает коллекцию.
     */
    public static boolean clear(String user_login) {
        lock.lock();
        try {
            if (DatabaseFacade.getInstance().clearLabWorks(user_login)) {
                labworks = DatabaseFacade.getInstance().getLabWorks();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Вставляет новый элемент в коллекцию.
     * @param key ключ элемента
     * @param lab объект лабораторной работы
     * @return true, если вставка прошла успешно
     */
    public static boolean insert(String user_login, Integer key, LabWork lab) {
        lock.lock();
        try {
            if (lab.getCreationDate() == null) {
                lab.setCreationDate(new Date());
            }
            boolean isSuccess = DatabaseFacade.getInstance().addLabWork(user_login, lab, key);
            if (isSuccess) {
                labworks.put(key, lab);
            }
            return isSuccess;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Удаляет элемент из коллекции по ключу.
     * @param key ключ элемента
     */
    public static boolean removeByKey(String user_login, Integer key){
        lock.lock();
        try {
            boolean isSuccess = DatabaseFacade.getInstance().deleteLabWork(user_login, key);
            if (isSuccess) {
                labworks.remove(key);
            }
            return isSuccess;
        } finally {
            lock.unlock();
        }
    }


    /**
     * Удаляет все элементы, ключ которых меньше заданного.
     * @param key пороговое значение ключа
     * @return количество удаленных элементов
     */
    public static int removeLowerKeys(String user_login, Integer key) {
        lock.lock();
        try {
            java.util.List<Integer> keysToRemove = labworks.keySet().stream()
                    .filter(k -> k < key)
                    .collect(java.util.stream.Collectors.toList());
            int removed = 0;
            for (Integer k : keysToRemove) {
                boolean isSuccess = DatabaseFacade.getInstance().deleteLabWork(user_login, k);
                if (isSuccess) {
                    labworks.remove(k);
                    removed++;
                }
            }
            return removed;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Перезагружает коллекцию из файла.
     */
    public static void reload() {
        lock.lock();
        try {
            labworks = DatabaseFacade.getInstance().getLabWorks();
        } finally {
            lock.unlock();
        }
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
        lock.lock();
        try {
            return labworks;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Возвращает время создания коллекции.
     * @return дата и время создания
     */
    public static Date getCollectionCreationTime(){
        lock.lock();
        try {
            return collectionCreationTime;
        } finally {
            lock.unlock();
        }
    }

    
}
