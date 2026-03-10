package com.akira.general.commands;

import com.akira.general.commands.interfaces.Command;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Команда вывода сложности всех элементов в порядке убывания.
 */
public class PrintFieldDescendingDifficultyCommand implements Command {
    @Override
    public Response execute(CollectionManager collectionManager) {
        String result = CollectionManager.getCollection().values().stream()
                .map(lw -> lw.getDifficulty())
                .filter(java.util.Objects::nonNull)
                .sorted(Comparator.reverseOrder())
                .map(Enum::toString)
                .collect(Collectors.joining("\n"));
        
        return new Response("Сложности в порядке убывания:\n" + result, true);
    }

    @Override
    public String describe() {
        return "print_field_descending_difficulty : вывести все значения поля difficulty в порядке убывания";
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
