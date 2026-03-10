package com.akira.general;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import com.akira.general.datas.*;

/**
 * Класс для интерактивного чтения данных лабораторной работы из консоли.
 */
public class LabWorkReader {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private final Scanner sc;

    static {
        DATE_FORMAT.setLenient(false);
    }

    public LabWorkReader(Scanner sc) {
        this.sc = sc;
    }

    /**
     * Читает данные для создания нового объекта LabWork из консоли.
     * @return новый объект LabWork
     */
    public LabWork readLabWork() {
        LabWork lab = new LabWork();
        // Дата создания и ID генерируются на сервере (согласно ТЗ ЛР6)
        
        lab.setName(readName());
        lab.setCoordinates(readCoordinates());
        lab.setMinimalPoint(readMinimalPoint());
        lab.setMaximumPoint(readMaximumPoint());
        lab.setDescription(readDescription());
        lab.setDifficulty(readDifficulty());
        lab.setAuthor(readPerson());

        return lab;
    }

    private String readName() {
        while (true) {
            System.out.print("Введите name (String, не пустое): ");
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("Ошибка: имя не может быть пустым.");
        }
    }

    private Coordinates readCoordinates() {
        Coordinates coords = new Coordinates();
        coords.setX(readCoordinateX());
        coords.setY(readCoordinateY());
        return coords;
    }

    private Integer readCoordinateX() {
        while (true) {
            System.out.print("Введите coordinates.x (Integer, > -881): ");
            String line = sc.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val > -881) return val;
                System.out.println("Ошибка: значение должно быть больше -881.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    private Long readCoordinateY() {
        while (true) {
            System.out.print("Введите coordinates.y (Long): ");
            String line = sc.nextLine().trim();
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    private Float readMinimalPoint() {
        while (true) {
            System.out.print("Введите minimalPoint (Float, > 0, пустая строка — null): ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) return null;
            try {
                float val = Float.parseFloat(line);
                if (val > 0) return val;
                System.out.println("Ошибка: значение должно быть больше 0.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число.");
            }
        }
    }

    private long readMaximumPoint() {
        while (true) {
            System.out.print("Введите maximumPoint (long, > 0): ");
            String line = sc.nextLine().trim();
            try {
                long val = Long.parseLong(line);
                if (val > 0) return val;
                System.out.println("Ошибка: значение должно быть больше 0.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    private String readDescription() {
        System.out.print("Введите description (String, пустая строка — null): ");
        String line = sc.nextLine().trim();
        return line.isEmpty() ? null : line;
    }

    private Difficulty readDifficulty() {
        while (true) {
            System.out.println("Доступные значения Difficulty: EASY, HARD, VERY_HARD, INSANE");
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
        Person person = new Person();
        person.setName(readAuthorName());
        person.setBirthday(readBirthday());
        person.setLocation(readLocation());
        return person;
    }

    private String readAuthorName() {
        while (true) {
            System.out.print("Введите author.name (String, не пустое): ");
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("Ошибка: имя автора не может быть пустым.");
        }
    }

    private Date readBirthday() {
        while (true) {
            System.out.print("Введите author.birthday (дд.мм.гггг, пустая строка — null): ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) return null;
            try {
                return DATE_FORMAT.parse(line);
            } catch (ParseException e) {
                System.out.println("Ошибка: неверный формат даты (дд.мм.гггг).");
            }
        }
    }

    private Location readLocation() {
        System.out.print("Хотите ввести author.location? (да/нет): ");
        String line = sc.nextLine().trim();
        if (!line.equalsIgnoreCase("да")) return null;
        Location loc = new Location();
        loc.setX(readLocationX());
        loc.setY(readLocationY());
        loc.setZ(readLocationZ());
        return loc;
    }

    private Integer readLocationX() {
        while (true) {
            System.out.print("Введите location.x (Integer): ");
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    private float readLocationY() {
        while (true) {
            System.out.print("Введите location.y (float): ");
            String line = sc.nextLine().trim();
            try {
                return Float.parseFloat(line);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число.");
            }
        }
    }

    private double readLocationZ() {
        while (true) {
            System.out.print("Введите location.z (double): ");
            String line = sc.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число.");
            }
        }
    }
}
