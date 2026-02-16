package com.akira;

public class Coordinates {
    private Integer x; //Значение поля должно быть больше -881, Поле не может быть null
    private Long y; //Поле не может быть null

    @Override
    public String toString() {
        return String.format("Coordinates{ x= %d , y= %d }", this, x.intValue(), y.longValue());
    }
}
