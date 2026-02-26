package com.akira.commands;

import java.util.ArrayList;
import java.util.Hashtable;

import com.akira.CollectionManager;
import com.akira.LabWork;
import com.akira.LabWorkReader;

/**
 * Команда замены элемента при большем значении.
 * <p>
 * Заменяет значение по ключу, если новое значение больше старого.
 * Сравнение выполняется по полю maximumPoint через метод {@link com.akira.LabWork#compareTo}.
 * </p>
 */
public class ReplaceGreatestCommand implements Command, Modable {
    /** Список аргументов команды */
    private ArrayList<String> args = new ArrayList<>();

    /**
     * Выполняет команду replace_if_greater.
     * <p>
     * Проверяет существование элемента с указанным ключом,
     * запрашивает новые данные и выполняет замену только если
     * новый элемент больше старого по значению maximumPoint.
     * </p>
     */
    @Override
    public void execute() {
        try {
            Integer key = Integer.parseInt(args.get(0));
            Hashtable<Integer, LabWork> coll = CollectionManager.getCollection();
            if (!coll.containsKey(key)) {
                System.out.println("Элемент с ключом " + key + " не найден.");
                return;
            }
            LabWork newLab = LabWorkReader.readLabWork();
            LabWork oldLab = coll.get(key);
            if (newLab.compareTo(oldLab) > 0) {
                newLab.setCreationDate(oldLab.getCreationDate());
                CollectionManager.update(key, newLab);
                System.out.println("Значение заменено.");
            } else {
                System.out.println("Новое значение не больше старого. Замена не выполнена.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ключ должен быть целым числом.");
        }
    }

    /**
     * Выводит описание команды.
     */
    @Override
    public void describe() {
        System.out.println("replace_if_greater {key} : заменить значение по ключу, если новое значение больше старого");
    }

    /**
     * Возвращает количество требуемых аргументов.
     *
     * @return 1 — команда требует один аргумент (ключ)
     */
    @Override
    public int numberArgsRequired() {
        return 1;
    }

    /**
     * Устанавливает аргументы команды.
     *
     * @param ar список аргументов командной строки
     */
    @Override
    public void setArguments(ArrayList<String> ar) {
        this.args = ar;
    }
}
