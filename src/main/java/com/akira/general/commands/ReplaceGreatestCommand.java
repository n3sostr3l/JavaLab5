package com.akira.general.commands;

import java.util.ArrayList;
import java.util.Hashtable;
import com.akira.general.commands.interfaces.Modable;
import com.akira.general.commands.interfaces.ObjectModable;
import com.akira.general.datas.LabWork;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;

/**
 * Команда замены элемента при большем значении.
 */
public class ReplaceGreatestCommand implements Modable, ObjectModable {
    private ArrayList<String> args = new ArrayList<>();
    private LabWork labWork;

    @Override
    public Response execute(CollectionManager collectionManager) {
        try {
            if (args.isEmpty()) return new Response("Ошибка: не указан ключ.", false);
            Integer key = Integer.parseInt(args.get(0));
            Hashtable<Integer, LabWork> coll = CollectionManager.getCollection();
            if (!coll.containsKey(key)) {
                return new Response("Ошибка: элемент с ключом " + key + " не найден.", false);
            }
            if (labWork == null) {
                return new Response("Ошибка: объект для сравнения не получен.", false);
            }
            LabWork oldLab = coll.get(key);
            if (labWork.compareTo(oldLab) > 0) {
                labWork.setCreationDate(oldLab.getCreationDate());
                CollectionManager.update(key, labWork);
                return new Response("Значение успешно заменено.", true);
            } else {
                return new Response("Новое значение не больше старого. Замена не выполнена.", true);
            }
        } catch (NumberFormatException e) {
            return new Response("Ошибка: ключ должен быть целым числом.", false);
        }
    }

    @Override
    public String describe() {
        return "replace_if_greater {key} {element} : заменить значение по ключу, если новое значение больше старого";
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
