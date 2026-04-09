package com.akira.server.commands;

import com.akira.server.commands.interfaces.Command;
import com.akira.server.managers.CollectionManager;
import com.akira.general.network.Response;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Команда группировки по значению maximumPoint.
 */
public class GroupCountingByMaximumPointCommand implements Command {
    /**
     * Выполняет группировку элементов.
     * @param collectionManager менеджер коллекции
     * @return ответ с результатами группировки
     */
    @Override
    public Response execute(CollectionManager collectionManager) {
        Map<Long, Long> counts = CollectionManager.getCollection().values().stream()
                .collect(Collectors.groupingBy(lw -> lw.getMaximumPoint(), Collectors.counting()));
        
        StringBuilder result = new StringBuilder("Количество элементов в каждой группе maximumPoint:\n");
        counts.forEach((point, count) -> result.append(point).append(": ").append(count).append("\n"));
        
        return new Response(result.toString(), true);
    }

    /**
     * Возвращает описание команды.
     * @return строка описания
     */
    @Override
    public String describe() {
        return "group_counting_by_maximum_point : сгруппировать элементы по maximumPoint и вывести количество в группах";
    }

    /**
     * Возвращает количество аргументов.
     * @return 0
     */
    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
