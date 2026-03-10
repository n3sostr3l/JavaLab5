package com.akira.general.commands;

import java.util.ArrayList;
import com.akira.general.commands.interfaces.Modable;
import com.akira.general.commands.interfaces.ObjectModable;
import com.akira.general.datas.LabWork;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;

/**
 * Команда добавления нового элемента в коллекцию.
 */
public class InsertCommand implements Modable, ObjectModable {
    private ArrayList<String> args = new ArrayList<>();
    private LabWork labWork;

    /**
     * Конструктор по умолчанию.
     */
    public InsertCommand() {}

    @Override
    public Response execute(CollectionManager collectionManager) {
        try {
            if (args.isEmpty()) {
                return new Response("Ошибка: не указан ключ.", false);
            }
            Integer key = Integer.parseInt(args.get(0));
            if (labWork == null) {
                return new Response("Ошибка: объект для вставки не получен.", false);
            }
            if (CollectionManager.insert(key, labWork)) {
                return new Response("Элемент успешно добавлен с ключом: " + key, true);
            } else {
                return new Response("Ошибка: элемент с таким ключом уже существует.", false);
            }
        } catch (NumberFormatException e) {
            return new Response("Ошибка: ключ должен быть целым числом.", false);
        }
    }

    @Override
    public String describe() {
        return "insert {key} {element} : добавить новый элемент с заданным ключом";
    }

    @Override
    public int numberArgsRequired() {
        return 1;
    }

    @Override
    public void setArguments(ArrayList<String> ar) {
        this.args = ar;
    }

    @Override
    public void setObject(LabWork labWork) {
        this.labWork = labWork;
    }
}
