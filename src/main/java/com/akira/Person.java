package com.akira;

public class Person {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private java.util.Date birthday; //Поле может быть null
    private Location location; //Поле может быть null

    @Override
    public String toString() {
        return String.format("""
                Person{
                name= %s ,
                birthday= %t ,
                location= %s
                """, name, birthday, location.toString());
    }
}