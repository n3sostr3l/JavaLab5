package com.akira.general.commands;

import java.util.ArrayList;
import com.akira.general.commands.interfaces.Modable;
import com.akira.general.commands.interfaces.ObjectModable;
import com.akira.general.datas.LabWork;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;

/**
 * Команда обновления элемента коллекции по id.
 */
public class UpdateCommand implements Modable, ObjectModable {
    private ArrayList<String> args = new ArrayList<>();
    private LabWork labWork;

    @Override
    public Response execute(CollectionManager collectionManager) {
        try {
            if (args.isEmpty()) {
                return new Response("Ошибка: не указан id.", false);
            }
            Long id = Long.parseLong(args.get(0));
            if (labWork == null) {
                return new Response("Ошибка: объект для обновления не получен.", false);
            }
            
            Integer key = CollectionManager.getCollection().entrySet().stream()
                    .filter(entry -> entry.getValue().getId().equals(id))
                    .map(entry -> entry.getKey())
                    .findFirst()
                    .orElse(null);

            if (key == null) {
                return new Response("Ошибка: элемент с таким id не найден.", false);
            }

            if (CollectionManager.update(key, labWork)) {
                return new Response("Элемент с id " + id + " успешно обновлен.", true);
            } else {
                return new Response("Ошибка при обновлении элемента.", false);
            }
        } catch (NumberFormatException e) {
            return new Response("Ошибка: id должен быть числом.", false);
        }
    }

    @Override
    public String describe() {
        return "update id {element} : обновить значение элемента коллекции, id которого равен заданному";
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
