package com.akira.general.commands.interfaces;

import com.akira.server.CollectionManager;
import com.akira.general.network.Response;
import java.io.Serializable;

/**
 * Базовый интерфейс для всех команд приложения.
 * <p>
 * Реализует Serializable для передачи объекта команды между клиентом и сервером.
 * </p>
 */
public interface Command extends Serializable {
    /**
     * Выполняет команду на стороне сервера.
     *
     * @param collectionManager менеджер коллекции сервера
     * @return объект Response с результатом выполнения
     */
    public Response execute(CollectionManager collectionManager);

    /**
     * Возвращает описание команды и ее синтаксис.
     *
     * @return строка с описанием команды
     */
    public String describe();

    /**
     * Возвращает количество требуемых строковых аргументов.
     *
     * @return количество аргументов
     */
    public int numberArgsRequired();
}
