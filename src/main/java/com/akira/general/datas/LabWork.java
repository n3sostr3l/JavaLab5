package com.akira.general.datas;

import java.io.IOError;
import java.io.Serializable;

/**
 * Класс, представляющий лабораторную работу.
 * <p>
 * Является основным элементом коллекции, управляемой программой.
 * Реализует интерфейс {@link Comparable} для сортировки по умолчанию
 * по полю maximumPoint.
 * </p>
 */
public class LabWork implements Comparable<LabWork>, Serializable{
    private static final long serialVersionUID = 1L;
    /** Счётчик для автоматической генерации уникальных идентификаторов */
    private static long idCounter = 1;
    /** Уникальный идентификатор лабораторной работы. Не может быть null, значение должно быть больше 0 */
    private Long id;
    /** Название лабораторной работы. Не может быть null, строка не может быть пустой */
    private String name;
    /** Координаты лабораторной работы. Не может быть null */
    private Coordinates coordinates;
    /** Дата создания. Не может быть null, генерируется автоматически */
    private java.util.Date creationDate;
    /** Минимальный балл. Может быть null, значение должно быть больше 0 */
    private Float minimalPoint;
    /** Максимальный балл. Значение должно быть больше 0 */
    private long maximumPoint;
    /** Описание лабораторной работы. Может быть null */
    private String description;
    /** Уровень сложности. Может быть null */
    private Difficulty difficulty;
    /** Автор лабораторной работы. Не может быть null */
    private Person author;

    /**
     * Создаёт новый объект LabWork с автоматически генерируемым id.
     */
    public LabWork(){
        this.id = idCounter++;
    }

    /**
     * Возвращает строковое представление лабораторной работы.
     *
     * @return строковое представление объекта
     */
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

    /**
     * Сравнивает эту лабораторную работу с другой по полю maximumPoint.
     *
     * @param labWork другая лабораторная работа для сравнения
     * @return отрицательное число, ноль или положительное число, если эта работа
     *         меньше, равна или больше заданной
     */
    @Override
    public int compareTo(LabWork labWork) {
        return Long.compare(this.maximumPoint, labWork.maximumPoint);
    }

    /**
     * Устанавливает координаты лабораторной работы.
     *
     * @param coordinates объект координат
     * @throws IOError если произошла ошибка ввода-вывода
     */
    public void setCoordinates(Coordinates coordinates) throws IOError{
        this.coordinates = coordinates;
    }

    /**
     * Устанавливает автора лабораторной работы.
     *
     * @param author объект автора
     */
    public void setAuthor(Person author) {
        this.author = author;
    }

    /**
     * Устанавливает название лабораторной работы.
     *
     * @param name название работы
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Устанавливает описание лабораторной работы.
     *
     * @param description описание работы
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Устанавливает уровень сложности лабораторной работы.
     *
     * @param difficulty уровень сложности
     */
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Устанавливает максимальный балл лабораторной работы.
     *
     * @param maximumPoint максимальный балл
     */
    public void setMaximumPoint(long maximumPoint) {
        this.maximumPoint = maximumPoint;
    }

    /**
     * Устанавливает минимальный балл лабораторной работы.
     *
     * @param minimalPoint минимальный балл
     */
    public void setMinimalPoint(Float minimalPoint) {
        this.minimalPoint = minimalPoint;
    }

    /**
     * Устанавливает дату создания лабораторной работы.
     *
     * @param creationDate дата создания
     */
    public void setCreationDate(java.util.Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Возвращает дату создания лабораторной работы.
     *
     * @return дата создания
     */
    public java.util.Date getCreationDate() {
        return creationDate;
    }

    /**
     * Устанавливает уникальный идентификатор лабораторной работы.
     *
     * @param id идентификатор
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Возвращает уникальный идентификатор лабораторной работы.
     *
     * @return идентификатор
     */
    public Long getId() {
        return id;
    }

    /**
     * Возвращает название лабораторной работы.
     *
     * @return название работы
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает максимальный балл лабораторной работы.
     *
     * @return максимальный балл
     */
    public long getMaximumPoint() {
        return maximumPoint;
    }

    /**
     * Возвращает автора лабораторной работы.
     *
     * @return объект автора
     */
    public Person getAuthor() {
        return author;
    }

    /**
     * Возвращает уровень сложности лабораторной работы.
     *
     * @return уровень сложности
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Возвращает координаты лабораторной работы.
     *
     * @return объект координат
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }
}