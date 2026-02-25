package com.akira.commands;

public class InfoCommand implements Command{
    @Override
    public void execute(){

        System.out.printf(
                """
                Информация о коллекции:
                Тип: %s
                Дата создания: %s,
                Количество элементов в коллекции: %d,
                
                """ // CollectionManager.getCollection().toString(), CollectionManager.getCollectionCreationTime(), CollectionManager.getCollection().size()
        );
    }

    @Override
    public void describe() {
        System.out.println("info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
