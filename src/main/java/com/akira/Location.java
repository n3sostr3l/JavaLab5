package com.akira;

public class Location {
    private Integer x; //Поле не может быть null
    private float y;
    private double z;

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("""
                Location{
                x= %s ,
                y= %f ,
                z= %f 
                }
                """, x, y, z);
    }
}