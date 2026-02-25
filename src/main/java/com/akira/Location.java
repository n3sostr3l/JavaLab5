package com.akira;

/**
 * Класс, представляющий местоположение.
 * <p>
 * Содержит трёхмерные координаты (X, Y, Z) для указания местоположения.
 * </p>
 */
public class Location {
    /** Координата X. Не может быть null */
    private Integer x;
    /** Координата Y */
    private float y;
    /** Координата Z */
    private double z;

    /**
     * Устанавливает координату X.
     *
     * @param x значение координаты X
     */
    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * Устанавливает координату Y.
     *
     * @param y значение координаты Y
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Устанавливает координату Z.
     *
     * @param z значение координаты Z
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Возвращает строковое представление местоположения.
     *
     * @return строковое представление объекта
     */
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