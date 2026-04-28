package com.akira.general.datas;

import java.io.Serializable;

/**
 * Класс, представляющий координаты лабораторной работы.
 */
public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;
    /** Координата X (макс. значение 879) */
    private Integer x;
    /** Координата Y (не может быть null) */
    private Long y;

    /**
     * Возвращает координату X.
     *
     * @return координата X
     */
    public Integer getX() {
        return x;
    }

    /**
     * Устанавливает координату X.
     *
     * @param x координата X
     */
    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * Возвращает координату Y.
     *
     * @return координата Y
     */
    public Long getY() {
        return y;
    }

    /**
     * Устанавливает координату Y.
     *
     * @param y координата Y
     */
    public void setY(Long y) {
        this.y = y;
    }

    /**
     * Возвращает строковое представление координат.
     *
     * @return строковое представление
     */
    @Override
    public String toString() {
        return String.format("Coordinates<x = %d , y = %d>", x, y);
    }

}
