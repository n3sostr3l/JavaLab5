package com.akira.commands;

import com.akira.CollectionManager;
import com.akira.LabWork;

import java.util.ArrayList;

public class UpdateCommand implements Command, Modable{
    private ArrayList<String> args = new ArrayList<String>();

    public void execute(){
        LabWork lab = new LabWork();
        boolean is_success = CollectionManager.update(args.get(0), lab);
        if (is_success){
            System.out.println("Обновление успешно!");
        }
        else{
            System.out.println("Не удалось обновить");
        }
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
