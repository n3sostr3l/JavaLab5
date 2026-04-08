package com.akira.general.network;

import com.akira.general.datas.LabWork;
import java.io.Serializable;
import java.util.Hashtable;

/**
 * Класс, представляющий ответ от сервера к клиенту.
 * Содержит текстовое сообщение ответа, статус успеха и, опционально, коллекцию.
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Текстовое сообщение ответа */
    private final String message;
    /** Флаг успешности выполнения запроса */
    private final boolean success;
    /** Коллекция, передаваемая в ответе (может быть null) */
    private final Hashtable<Integer, LabWork> collection;

    /**
     * Конструктор ответа.
     * @param message сообщение от сервера
     * @param success флаг успешного выполнения
     */
    public Response(String message, boolean success) {
        this(message, success, null);
    }

    /**
     * Конструктор ответа с коллекцией.
     * @param message сообщение от сервера
     * @param success флаг успешного выполнения
     * @param collection коллекция объектов
     */
    public Response(String message, boolean success, Hashtable<Integer, LabWork> collection) {
        this.message = message;
        this.success = success;
        this.collection = collection;
    }

    /**
     * Возвращает сообщение от сервера.
     * @return сообщение
     */
    public String getMessage() {
        return message;
    }

    /**
     * Проверяет успешность выполнения команды.
     * @return true, если успешно
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Возвращает коллекцию объектов из ответа.
     * @return хеш-таблица объектов
     */
    public Hashtable<Integer, LabWork> getCollection() {
        return collection;
    }

    /**
     * Возвращает строковое представление ответа.
     * @return строковое представление
     */
    @Override
    public String toString() {
        return "Response[" + message + ", success=" + success + ", collection=" + (collection != null ? "present" : "null") + "]";
    }
}
