package com.akira;

import java.io.IOError;

public class LabWork implements Comparable<LabWork>{
    private static long idCounter = 1;
    private Long id;
    private String name;
    private Coordinates coordinates;
    private java.util.Date creationDate;
    private Float minimalPoint;
    private long maximumPoint;
    private String description;
    private Difficulty difficulty;
    private Person author;

    public LabWork(){
        this.id = idCounter++;
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