package com.akira;
public class Location {
    private Integer x; //Поле не может быть null
    private float y;
    private double z;

    @Override
    public String toString(){
        return String.format("""
                Location{
                x= %d ,
                y= %d ,
                z= %d 
                }
                """, x.intValue(), y, z);
    }
}