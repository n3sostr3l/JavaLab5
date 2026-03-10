package com.akira.general.commands;

import com.akira.general.commands.interfaces.Command;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Команда группировки по значению maximumPoint.
 */
public class GroupCountingByMaximumPointCommand implements Command {
    @Override
    public Response execute(CollectionManager collectionManager) {
        Map<Long, Long> counts = CollectionManager.getCollection().values().stream()
                .collect(Collectors.groupingBy(lw -> lw.getMaximumPoint(), Collectors.counting()));
        
        StringBuilder result = new StringBuilder("Количество элементов в каждой группе maximumPoint:\n");
        counts.forEach((point, count) -> result.append(point).append(": ").append(count).append("\n"));
        
        return new Response(result.toString(), true);
    }

    @Override
    public String describe() {
        return "group_counting_by_maximum_point : сгруппировать элементы по maximumPoint и вывести количество в группах";
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
