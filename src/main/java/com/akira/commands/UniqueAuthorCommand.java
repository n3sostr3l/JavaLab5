package com.akira.commands;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import com.akira.CollectionManager;
import com.akira.LabWork;
import com.akira.Person;

public class UniqueAuthorCommand implements Command{
    Set<Person> unique_authors = new HashSet<>();

    @Override
    public void execute() {
        Hashtable<Integer, LabWork> coll = CollectionManager.getCollection();
        for (LabWork lab : coll.values()){
            unique_authors.add(lab.getAuthor());
        }
        for (Person author : unique_authors){
            System.out.println(author);
        }
    }

    @Override
    public void describe() {
        System.out.println("print_unique_author : вывести уникальные значения поля author всех элементов в коллекции");
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
