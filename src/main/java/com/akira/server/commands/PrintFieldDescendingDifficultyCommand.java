package com.akira.server.commands;

import com.akira.server.commands.interfaces.Command;
import com.akira.server.managers.CollectionManager;
import com.akira.general.network.Response;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Команда вывода сложности всех элементов в порядке убывания.
 */
public class PrintFieldDescendingDifficultyCommand implements Command {
    /**
     * Выводит значения сложности всех элементов коллекции в порядке убывания.
     * @param collectionManager менеджер коллекции
     * @return ответ со списком сложностей
     */
    @Override
    public Response execute(CollectionManager collectionManager, String login) {
        String result = CollectionManager.getCollection().values().stream()
                .map(lw -> lw.getDifficulty())
                .filter(java.util.Objects::nonNull)
                .sorted(Comparator.reverseOrder())
                .map(Enum::toString)
                .collect(Collectors.joining("\n"));

        return new Response("Сложности в порядке убывания:\n" + result, true);
    }

    /**
     * Возвращает описание команды.
     * @return строка описания
     */
    @Override
    public String describe() {
        return "print_field_descending_difficulty : вывести все значения поля difficulty в порядке убывания";
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
