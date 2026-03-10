package com.akira.general.datas;

import java.io.Serializable;

/**
 * Класс, представляющий координаты лабораторной работы.
 * <p>
 * Содержит координаты X и Y для позиционирования объекта.
 * </p>
 */
public class Coordinates implements Serializable{
    /**  */
    private static final long serialVersionUID = 1L;
    /** Координата X. Значение должно быть больше -881, не может быть null */
    private Integer x;
    /** Координата Y. Не может быть null */
    private Long y;

    

    /**
     * Устанавливает координату X.
     *
     * @param x значение координаты X (должно быть больше -881)
     */
    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * Устанавливает координату Y.
     *
     * @param y значение координаты Y
     */
    public void setY(Long y) {
        this.y = y;
    }

    /**
     * Возвращает строковое представление координат.
     *
     * @return строковое представление объекта
     */
    @Override
    public String toString() {
        return String.format("Coordinates{ x= %d , y= %d }", x.intValue(), y.longValue());
    }
}
