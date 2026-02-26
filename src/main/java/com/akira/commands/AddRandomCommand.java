package com.akira.commands;

import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.ThreadLocalRandom;

import com.akira.CollectionManager;
import com.akira.Coordinates;
import com.akira.Difficulty;
import com.akira.LabWork;
import com.akira.Location;
import com.akira.Person;


/**
 * Команда генерации случайно лабораторной работы.
 * <p>
 * Записывает в коллекцию случайню лабораторную работу.
 * Создает лабораторную из определенных значений в списке и случайных чисел.
 * </p>
 */
public class AddRandomCommand implements Command{
    /** Список названий лабораторных работы */
    private static final String[] LAB_NAMES = {
            "Математический Анализ", "Линейная Алгебра", "Промпт Инженерия", "Программирование на Java",
            "Программирование на Python", "Алгоритмы и структуры данных", "Дискретная Математика", "Основы Профессиональной Деятельности"
    };
    /** Список описаний лабораторных работы */
    private static final String[] DESCRIPTIONS = {
            "Практическая работа", "Исследовательская работа", "Отчёт по теме", "Лабораторная работа", "Контрольная лабораторная", "Дополнительная работа"
    };
    /**
     * Выполняет команду add_random.
     * <p>
     * Создает случайный ключ, случайную лабораторную используя {@link #generateRandomLabWork()}
     * </p>
     */
    @Override
    public void execute(){
        Integer key = generateUniqueKey();
        LabWork randomLabWork = generateRandomLabWork();
        CollectionManager.insert(key, randomLabWork);
        System.out.println("Случайная лабораторная работа добавлена. Ключ: " + key + ", id: " + randomLabWork.getId());
    }

    /**
     * Выводит описание команды
     */
    @Override
    public void describe(){
        System.out.println("add_random - добавление случайно сгенерированной лабораторной работы в коллекцию");
    }
    /**
     * Возвращает количество требуемых аргументов.
     *
     * @return 0 — команда требует ноль аргументов
     */
    @Override
    public int numberArgsRequired(){
        return 0;
    }
    /**
     * Создает случайный ключ, которого нет в Hashtable
     *
     * @return key - уникальный ключ
     */
    private Integer generateUniqueKey() {
        Hashtable<Integer, LabWork> collection = CollectionManager.getCollection();
        Integer key;
        do {
            key = ThreadLocalRandom.current().nextInt(1, 100000);
        } while (collection.containsKey(key));
        return key;
    }
    /**
     * Создает случайную лабораторную работу
     *
     * @return labwork - сгенерированная лаб. работа
     */
    private LabWork generateRandomLabWork() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        LabWork labWork = new LabWork();
        labWork.setCreationDate(new Date());
        labWork.setName(randomName(random));
        labWork.setCoordinates(randomCoordinates(random));
        labWork.setMinimalPoint(random.nextFloat(1.0f, 51.0f));
        labWork.setMaximumPoint(random.nextLong(51, 201));
        labWork.setDescription(DESCRIPTIONS[random.nextInt(DESCRIPTIONS.length)]);
        labWork.setDifficulty(randomDifficulty(random));
        labWork.setAuthor(randomPerson(random));

        return labWork;
    }
    /**
     * Создает случайную лабораторную работу
     *
     * @return labwork - сгенерированная лаб. работа
     */
    private String randomName(ThreadLocalRandom random) {
        return LAB_NAMES[random.nextInt(LAB_NAMES.length)] + " " + random.nextInt(1, 101);
    }
    /**
     * Задает случайные координаты
     *
     * @param random - случайный генератор
     * 
     * @return coordinates - координаты по X, Y (с условиями)
     */
    private Coordinates randomCoordinates(ThreadLocalRandom random) {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(random.nextInt(-880, 1000));
        coordinates.setY(random.nextLong(-1000, 1000));
        return coordinates;
    }
    /**
     * Задает случайную сложность лабораторной работы  из enum Difficulty
     *
     * @param random - случайный генератор
     * 
     * @return сложность 
     */
    private Difficulty randomDifficulty(ThreadLocalRandom random) {
        Difficulty[] values = Difficulty.values();
        return values[random.nextInt(values.length)];
    }
    /**
     * Задает случайную сложность лабораторной работы  из enum Difficulty
     *
     * @param random - случайный генератор
     * 
     * @return сложность 
     */
    private Person randomPerson(ThreadLocalRandom random) {
        Person person = new Person();
        person.setName("Author_" + random.nextInt(1, 10000));
        person.setBirthday(randomBirthday(random));
        person.setLocation(randomLocation(random));
        return person;
    }
    /**
     * Задает случайную дату рождения автора
     *
     * @param random - случайный генератор
     * 
     * @return Date 
     */
    private Date randomBirthday(ThreadLocalRandom random) {
        long now = System.currentTimeMillis();
        long hundredYearsMillis = 100L * 365 * 24 * 60 * 60 * 1000;
        long timestamp = now - random.nextLong(hundredYearsMillis);
        return new Date(timestamp);
    }
    /**
     * Задает случайную локацию с координатами X, Y, Z
     *
     * @param random - случайный генератор
     * 
     * @return локация 
     */
    private Location randomLocation(ThreadLocalRandom random) {
        Location location = new Location();
        location.setX(random.nextInt(-880, 1001));
        location.setY((float) random.nextDouble(-500.0, 500.0));
        location.setZ(random.nextDouble(-500.0, 500.0));
        return location;
    }
}
