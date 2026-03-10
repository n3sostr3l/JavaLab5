package com.akira.commands;

import com.akira.general.commands.interfaces.Command;
import com.akira.server.CollectionManager;

/**
 * Команда вывода информации о коллекции.
 * <p>
 * Отображает тип коллекции, дату её инициализации
 * и текущее количество элементов.
 * </p>
 */
public class InfoCommand implements Command{
    /**
     * Выполняет команду info.
     * <p>
     * Выводит в консоль следующую информацию о коллекции:
     * <ul>
     *   <li>тип коллекции</li>
     *   <li>дата создания</li>
     *   <li>количество элементов</li>
     * </ul>
     */
    @Override
    public void execute(){

        System.out.printf(
                """
                Информация о коллекции:
                Тип: %s
                Дата создания: %s
                Количество элементов в коллекции: %d

                """, CollectionManager.getCollection().getClass().getSimpleName(), CollectionManager.getCollectionCreationTime(), CollectionManager.getCollection().size()
        );
    }

    /**
     * Выводит описание команды.
     */
    @Override
    public void describe() {
        System.out.println("info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
    }

    /**
     * Возвращает количество требуемых аргументов.
     *
     * @return 0 — команда не требует аргументов
     */
    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
