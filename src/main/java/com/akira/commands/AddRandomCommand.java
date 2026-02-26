package com.akira.commands;

public class AddRandomCommand implements Command{
    
    
    public void execute(){
        
    }

    
    public void describe(){
        System.out.println("add_random - добавление случайно сгенерированной лабораторной работы в коллекцию");
    }
    
    public int numberArgsRequired(){
        return 0;
    }   
    
}
