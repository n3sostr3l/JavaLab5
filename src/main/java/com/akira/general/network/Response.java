package com.akira.general.network;

import java.io.Serializable;

/**
 * Класс, представляющий ответ от сервера к клиенту.
 * Содержит текстовое сообщение ответа и статус успеха.
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String message;
    private final boolean success;

    /**
     * Конструктор ответа.
     * @param message сообщение от сервера
     * @param success флаг успешного выполнения
     */
    public Response(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "Response: " + message + (success ? " (Success)" : " (Failure)");
    }
}
