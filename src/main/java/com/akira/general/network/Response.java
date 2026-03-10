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

    private final String message;
    private final boolean success;
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

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public Hashtable<Integer, LabWork> getCollection() {
        return collection;
    }

    @Override
    public String toString() {
        return "Response[" + message + ", success=" + success + ", collection=" + (collection != null ? "present" : "null") + "]";
    }
}
