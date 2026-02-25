package com.akira.commands;

import com.akira.FileEditor;
import com.akira.LabWork;

import java.util.Hashtable;

import com.akira.CollectionManager;

public class SaveCommand implements Command{
    @Override
    public void execute() {
        Hashtable<String, LabWork> coll = CollectionManager.getCollection();
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
