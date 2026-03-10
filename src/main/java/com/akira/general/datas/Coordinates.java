package com.akira.general.datas;

import java.io.Serializable;

/**
 * Класс, представляющий координаты лабораторной работы.
 */
public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer x;
    private Long y;

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Long getY() {
        return y;
    }

    public void setY(Long y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("Coordinates{ x= %d , y= %d }", x, y);
    }
}
