package com.akira;

import java.io.IOError;

public class LabWork implements Comparable<LabWork>{
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Float minimalPoint; //Поле может быть null, Значение поля должно быть больше 0
    private long maximumPoint; //Значение поля должно быть больше 0
    private String description; //Поле может быть null
    private Difficulty difficulty; //Поле может быть null
    private Person author; //Поле не может быть null

    public LabWork(){
        this.id = (long) FileEditor.getCollection().size()+CollectionManager.getCollection().size();
    }

    @Override
    public String toString() {
        return String.format("""
                LabWork{ 
                id= %d , 
                name= %s , 
                coordinates = %s , 
                creationDate= %s , 
                minimalPoint= %s , 
                maximumPoint= %d ,
                description= %s ,
                difficulty= %s ,
                author= %s
                }
                """, id.longValue(), name, coordinates, creationDate, minimalPoint, maximumPoint, description, difficulty, author);
    }

    @Override
    public int compareTo(LabWork labWork) {
        return Long.compare(this.maximumPoint, labWork.maximumPoint);
    }

    public void setCoordinates(Coordinates coordinates) throws IOError{
        this.coordinates = coordinates;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setMaximumPoint(long maximumPoint) {
        this.maximumPoint = maximumPoint;
    }

    public void setMinimalPoint(Float minimalPoint) {
        this.minimalPoint = minimalPoint;
    }

    public void setCreationDate(java.util.Date creationDate) {
        this.creationDate = creationDate;
    }

    public java.util.Date getCreationDate() {
        return creationDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getMaximumPoint() {
        return maximumPoint;
    }

    public Person getAuthor() {
        return author;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
}