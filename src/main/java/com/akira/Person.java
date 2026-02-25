package com.akira;

/**
 * Класс, представляющий автора лабораторной работы.
 * <p>
 * Содержит информацию о имени, дне рождения и местоположении автора.
 * </p>
 */
public class Person {
    /** Имя автора. Не может быть null, строка не может быть пустой */
    private String name;
    /** Дата рождения автора. Может быть null */
    private java.util.Date birthday;
    /** Местоположение автора. Может быть null */
    private Location location;

    /**
     * Устанавливает имя автора.
     *
     * @param name имя автора
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Устанавливает дату рождения автора.
     *
     * @param birthday дата рождения
     */
    public void setBirthday(java.util.Date birthday) {
        this.birthday = birthday;
    }

    /**
     * Устанавливает местоположение автора.
     *
     * @param location объект местоположения
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Возвращает имя автора.
     *
     * @return имя автора
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает строковое представление автора.
     *
     * @return строковое представление объекта
     */
    @Override
    public String toString() {
        return String.format("""
                Person{
                name= %s ,
                birthday= %s ,
                location= %s
                }""", name, birthday, location);
    }
}