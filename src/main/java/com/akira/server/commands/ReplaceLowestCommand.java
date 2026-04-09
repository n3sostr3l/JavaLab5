package com.akira.server.commands;

import java.util.ArrayList;
import java.util.Hashtable;

import com.akira.server.commands.interfaces.Modable;
import com.akira.server.commands.interfaces.ObjectModable;
import com.akira.general.datas.LabWork;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;

/**
 * Команда замены элемента при меньшем значении.
 */
public class ReplaceLowestCommand implements Modable, ObjectModable {
    /** Аргументы команды */
    private ArrayList<String> args = new ArrayList<>();
    /** Объект лабораторной работы для сравнения */
    private LabWork labWork;

    /**
     * Выполняет замену элемента, если новое значение меньше текущего.
     * @param collectionManager менеджер коллекции
     * @return ответ с результатом замены
     */
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
            if (labWork.compareTo(oldLab) < 0) {
                labWork.setCreationDate(oldLab.getCreationDate());
                CollectionManager.update(key, labWork);
                return new Response("Значение успешно заменено.", true);
            } else {
                return new Response("Новое значение не меньше старого. Замена не выполнена.", true);
            }
        } catch (NumberFormatException e) {
            return new Response("Ошибка: ключ должен быть целым числом.", false);
        }
    }

    /**
     * Возвращает описание команды.
     * @return строка описания
     */
    @Override
    public String describe() {
        return "replace_if_lower {key} : заменить значение по ключу, если новое значение меньше старого. Сравнение выполняется по полю maximumPoint";
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
     * Устанавливает объект для сравнения.
     * @param labWork объект лабораторной работы
     */
    @Override
    public void setObject(LabWork labWork) {
        this.labWork = labWork;
    }
}
