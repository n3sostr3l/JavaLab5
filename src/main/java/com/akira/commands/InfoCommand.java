package com.akira.commands;

import com.akira.CollectionManager;

import java.util.Collection;

public class InfoCommand implements Command{
    @Override
    public void execute(){
        System.out.printf(
                """
                Информация о коллекции:
                Тип: %s
                Дата создания: %t,
                Количество элементов в коллекции: %d,
                
                """, CollectionManager.getCollection().toString(), дата создания коллекции, CollectionManager.getCollection().size()
        )
    }

    @Override
    public void describe() {

    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
