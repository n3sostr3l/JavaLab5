package com.akira;

public class Coordinates {
    private Integer x; //Значение поля должно быть больше -881, Поле не может быть null
    private Long y; //Поле не может быть null

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Long y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("Coordinates{ x= %d , y= %d }", x.intValue(), y.longValue());
    }
}
