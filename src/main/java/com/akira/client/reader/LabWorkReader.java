package com.akira.client.reader;

import java.time.LocalDate;
import java.util.Scanner;
import com.akira.general.datas.*;

/**
 * Класс для интерактивного чтения данных лабораторной работы из консоли.
 */
public class LabWorkReader {
    private final Scanner sc;

    /**
     * @param sc источник ввода
     */
    public LabWorkReader(Scanner sc) {
        this.sc = sc;
    }

    /**
     * Читает и возвращает новый объект {@link LabWork} из консоли.
     * ID и дата создания генерируются на сервере согласно ТЗ.
     *
     * @return новый объект LabWork
     */
    public LabWork readLabWork() {
        LabWork lab = new LabWork();
        lab.setName(readNonEmpty("name (String, не пустое)"));
        lab.setCoordinates(readCoordinates());
        lab.setMinimalPoint(readMinimalPoint());
        lab.setMaximumPoint(readLong("maximumPoint (long, > 0)", 0, Long.MAX_VALUE));
        lab.setDescription(readNullable("description (String, пустая строка — null)"));
        lab.setDifficulty(readDifficulty());
        lab.setAuthor(readPerson());
        return lab;
    }

    // --- Примитивные читатели ---

    /**
     * Читает непустую строку из консоли.
     *
     * @param prompt подсказка для пользователя
     * @return введенная непустая строка
     */
    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print("Введите " + prompt + ": ");
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("Ошибка: поле не может быть пустым.");
        }
    }

    /**
     * Читает строку из консоли, которая может быть пустой (трактуется как null).
     *
     * @param prompt подсказка для пользователя
     * @return введенная строка или null
     */
    private String readNullable(String prompt) {
        System.out.print("Введите " + prompt + ": ");
        String line = sc.nextLine().trim();
        return line.isEmpty() ? null : line;
    }

    /**
     * Читает целое число из консоли в заданном диапазоне.
     *
     * @param prompt подсказка для пользователя
     * @param min минимальное значение (исключая)
     * @param max максимальное значение (включая)
     * @return введенное целое число
     */
    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print("Введите " + prompt + ": ");
            try {
                int val = Integer.parseInt(sc.nextLine().trim());
                if (val > min && val <= max) return val;
                System.out.println("Ошибка: значение должно быть > " + min + ".");
            } catch (NumberFormatException e) {
                System.out.println(String.format("Ошибка: введите целое число из промежутка [%d,%d].", Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
        }
    }

    /**
     * Читает длинное целое число из консоли в заданном диапазоне.
     *
     * @param prompt подсказка для пользователя
     * @param min минимальное значение (исключая)
     * @param max максимальное значение (включая)
     * @return введенное длинное целое число
     */
    private long readLong(String prompt, long min, long max) {
        while (true) {
            System.out.print("Введите " + prompt + ": ");
            try {
                long val = Long.parseLong(sc.nextLine().trim());
                if (val > min && val <= max) return val;
                System.out.println("Ошибка: значение должно быть > " + min + ".");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    /**
     * Читает число с плавающей точкой (float) из консоли.
     *
     * @param prompt подсказка для пользователя
     * @return введенное число
     */
    private float readFloat(String prompt) {
        while (true) {
            System.out.print("Введите " + prompt + ": ");
            try {
                return Float.parseFloat(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число.");
            }
        }
    }

    /**
     * Читает число с плавающей точкой двойной точности (double) из консоли.
     *
     * @param prompt подсказка для пользователя
     * @return введенное число
     */
    private double readDouble(String prompt) {
        while (true) {
            System.out.print("Введите " + prompt + ": ");
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число.");
            }
        }
    }

    // --- Составные читатели ---

    /**
     * Читает координаты из консоли.
     *
     * @return объект координат
     */
    private Coordinates readCoordinates() {
        Coordinates c = new Coordinates();
        c.setX(readInt("coordinates.x (Integer, > -881)", -881, Integer.MAX_VALUE));
        c.setY(readLong("coordinates.y (Long)", Long.MIN_VALUE, Long.MAX_VALUE));
        return c;
    }

    /**
     * Читает минимальный балл из консоли.
     *
     * @return минимальный балл или null
     */
    private Float readMinimalPoint() {
        while (true) {
            System.out.print("Введите minimalPoint (Float, > 0, пустая строка — null): ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) return null;
            try {
                float val = Float.parseFloat(line);
                if (val > 0) return val;
                System.out.println("Ошибка: значение должно быть > 0.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число.");
            }
        }
    }

    /**
     * Читает уровень сложности из консоли.
     *
     * @return уровень сложности или null
     */
    private Difficulty readDifficulty() {
        while (true) {
            System.out.println("Доступные значения Difficulty: " + java.util.Arrays.toString(Difficulty.values()));
            System.out.print("Введите difficulty (пустая строка — null): ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) return null;
            try {
                return Difficulty.valueOf(line.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: нет такой константы.");
            }
        }
    }

    /**
     * Читает данные автора из консоли.
     *
     * @return объект автора
     */
    private Person readPerson() {
        Person p = new Person();
        p.setName(readNonEmpty("author.name (String, не пустое)"));
        p.setBirthday(readBirthday());
        p.setLocation(readLocation());
        return p;
    }

    /**
     * Читает дату рождения из консоли.
     *
     * @return дата рождения или null
     */
    private LocalDate readBirthday() {
        while (true) {
            System.out.print("Введите author.birthday (дд.мм.гггг, пустая строка — null): ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) return null;
            try {
                return DateParser.parseAndValidate(line);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неверный формат даты (дд.мм.гггг).");
            }
        }
    }

    /**
     * Читает ответ "да" или "нет" из консоли.
     *
     * @param question вопрос для пользователя
     * @return введенная строка
     */
    private String readYesNo(String question) {
        
        System.out.print(String.format("%s (y/n) (по умолчанию - n): ", question));
        String line = sc.nextLine().trim();

        return line;

    }

    /**
     * Читает местоположение из консоли.
     *
     * @return объект местоположения или null
     */
    private Location readLocation() {
        String answer = readYesNo("Хотите ввести author.location?");
        if (!answer.equalsIgnoreCase("y") && !answer.equalsIgnoreCase("yes")) return null;
        Location loc = new Location();
        loc.setX(readInt("location.x (Integer)", Integer.MIN_VALUE, Integer.MAX_VALUE));
        loc.setY(readFloat("location.y (float)"));
        loc.setZ(readDouble("location.z (double)"));
        return loc;
    }
}