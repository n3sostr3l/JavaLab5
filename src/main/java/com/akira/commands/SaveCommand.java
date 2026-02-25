package com.akira.commands;

import java.util.Hashtable;

import com.akira.CollectionManager;
import com.akira.FileEditor;
import com.akira.LabWork;

public class SaveCommand implements Command{
    @Override
    public void execute() {
        Hashtable<Integer, LabWork> coll = CollectionManager.getCollection();
        FileEditor.saveCollection(coll);
    }

    @Override
    public void describe() {
        System.out.println("save : сохранить коллекцию в файл");
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
