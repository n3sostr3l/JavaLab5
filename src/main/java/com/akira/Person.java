package com.akira;

public class Person {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private java.util.Date birthday; //Поле может быть null
    private Location location; //Поле может быть null

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthday(java.util.Date birthday) {
        this.birthday = birthday;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("""
                Person{
                name= %s ,
                birthday= %s ,
                location= %s
                }""", name, birthday, location);
    }
}