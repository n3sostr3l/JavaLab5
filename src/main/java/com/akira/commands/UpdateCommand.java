package com.akira.commands;

import com.akira.FileEditor;
import com.akira.CollectionManager;
import com.akira.LabWork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import com.akira.commands.Modable;

public class UpdateCommand implements Command, Modable{
    private ArrayList<String> args = new ArrayList<String>();

    public void execute(){
        Hashtable<String, LabWork> coll = CollectionManager.getCollection();
    }

    public void describe(){
        System.out.println("update {id} {element} : обновить значение элемента коллекции, id которого равен заданному");
    }
    @Override
    public int numberArgsRequired(){
        return 2;
    }
    @Override
    public void setArguments(ArrayList<String> ar){
        this.args = ar;
    }
}
