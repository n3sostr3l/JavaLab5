package com.akira.general;

import java.text.ParseException;
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
        lab.setMaximumPoint(readLong("maximumPoint (long, > 0)", 1, Long.MAX_VALUE));
        lab.setDescription(readNullable("description (String, пустая строка — null)"));
        lab.setDifficulty(readDifficulty());
        lab.setAuthor(readPerson());
        return lab;
    }

    // --- Примитивные читатели ---

    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print("Введите " + prompt + ": ");
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("Ошибка: поле не может быть пустым.");
        }
    }

    private String readNullable(String prompt) {
        System.out.print("Введите " + prompt + ": ");
        String line = sc.nextLine().trim();
        return line.isEmpty() ? null : line;
    }

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

    private long readLong(String prompt, long min, long max) {
        while (true) {
            System.out.print("Введите " + prompt + ": ");
            try {
                long val = Long.parseLong(sc.nextLine().trim());
                if (val > min && val <= max) return val;
                System.out.println("Ошибка: значение должно быть >= " + min + ".");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

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

    private Coordinates readCoordinates() {
        Coordinates c = new Coordinates();
        c.setX(readInt("coordinates.x (Integer, > -881)", -881, Integer.MAX_VALUE));
        c.setY(readLong("coordinates.y (Long)", Long.MIN_VALUE, Long.MAX_VALUE));
        return c;
    }

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

    private Person readPerson() {
        Person p = new Person();
        p.setName(readNonEmpty("author.name (String, не пустое)"));
        p.setBirthday(readBirthday());
        p.setLocation(readLocation());
        return p;
    }

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

    private String readYesNo(String question) {
        while (true) {
            System.out.print(String.format("%s (да/нет)", question));
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("Ответ должен быть не пуст");
            }

            try {
                if (line.equalsIgnoreCase("да") || line.equalsIgnoreCase("нет")) {
                    return line;
                } else
                    throw new IllegalArgumentException("Ошибка ввода, введите 'да' или 'нет' (без кавычек)");
            } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
            }
        }

    }

    private Location readLocation() {
        if (readYesNo("Хотите ввести author.location?").equalsIgnoreCase("нет")) return null;
        Location loc = new Location();
        loc.setX(readInt("location.x (Integer)", Integer.MIN_VALUE, Integer.MAX_VALUE));
        loc.setY(readFloat("location.y (float)"));
        loc.setZ(readDouble("location.z (double)"));
        return loc;
    }
}