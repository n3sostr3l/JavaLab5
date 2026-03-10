package com.akira.commands;

import java.util.Hashtable;

import com.akira.general.commands.interfaces.Command;
import com.akira.general.datas.LabWork;
import com.akira.server.CollectionManager;
import com.akira.server.FileEditor;

/**
 * Команда сохранения коллекции в файл.
 * <p>
 * Сохраняет текущее состояние коллекции лабораторных работ
 * в XML-файл, используемый для персистентного хранения данных.
 * </p>
 */
public class SaveCommand implements Command{
    /**
     * Выполняет команду save.
     * <p>
     * Получает текущую коллекцию из {@link CollectionManager}
     * и сохраняет её в файл с помощью {@link FileEditor}.
     * </p>
     */
    @Override
    public void execute() {
        Hashtable<Integer, LabWork> coll = CollectionManager.getCollection();
        FileEditor.saveCollection(coll);
    }

    /**
     * Выводит описание команды.
     */
    @Override
    public void describe() {
        System.out.println("save : сохранить коллекцию в файл");
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
