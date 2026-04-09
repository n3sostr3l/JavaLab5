package com.akira.server.commands;

import java.util.ArrayList;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.commands.interfaces.ObjectModable;
import com.akira.server.managers.CollectionManager;
import com.akira.general.datas.LabWork;
import com.akira.general.network.Response;

/**
 * Команда добавления нового элемента в коллекцию.
 */
public class InsertCommand implements Modable, ObjectModable {
    /** Аргументы команды */
    private ArrayList<String> args = new ArrayList<>();
    /** Объект лабораторной работы для вставки */
    private LabWork labWork;

    /**
     * Выполняет вставку элемента.
     * @param collectionManager менеджер коллекции
     * @return ответ с результатом вставки
     */
    @Override
    public Response execute(CollectionManager collectionManager) {
        try {
            Integer key = Integer.parseInt(args.get(0));
            if (labWork == null) {
                return new Response("Ошибка: объект для вставки не получен.", false);
            }
            collectionManager.insert(key, labWork);
            return new Response(String.format("Элемент успешно добавлен/изменен с ключом %d и id= %d" , key, labWork.getId()), true);
            
        } catch (NumberFormatException e) {
            return new Response("Ошибка: ключ должен быть целым числом или превышен лимит числа.", false);
        }
    }

    /**
     * Возвращает описание команды.
     * @return строка описания
     */
    @Override
    public String describe() {
        return "insert {key} {element} : добавить новый / заменить существующий элемент по ключу";
    }

    /**
     * Возвращает количество аргументов.
     * @return 1
     */
    @Override
    public int numberArgsRequired() {
        return 1;
    }

    /**
     * Устанавливает аргументы команды.
     * @param ar список аргументов
     */
    @Override
    public void setArguments(ArrayList<String> ar) {
        this.args = ar;
    }

    /**
     * Устанавливает объект для вставки.
     * @param labWork объект лабораторной работы
     */
    @Override
    public void setObject(LabWork labWork) {
        this.labWork = labWork;
    }
}
