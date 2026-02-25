package com.akira;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import com.akira.commands.CommandInvoker;

public class LabWorkReader {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    static {
        DATE_FORMAT.setLenient(false);
    }

    public static LabWork readLabWork() {
        Scanner sc = CommandInvoker.getScanner();
        LabWork lab = new LabWork();
        lab.setCreationDate(new Date());

        lab.setName(readName(sc));
        lab.setCoordinates(readCoordinates(sc));
        lab.setMinimalPoint(readMinimalPoint(sc));
        lab.setMaximumPoint(readMaximumPoint(sc));
        lab.setDescription(readDescription(sc));
        lab.setDifficulty(readDifficulty(sc));
        lab.setAuthor(readPerson(sc));

        return lab;
    }

    private static String readName(Scanner sc) {
        while (true) {
            System.out.print("Введите name (String, не пустое): ");
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Ошибка: имя не может быть пустым.");
        }
    }

    private static Coordinates readCoordinates(Scanner sc) {
        Coordinates coords = new Coordinates();
        coords.setX(readCoordinateX(sc));
        coords.setY(readCoordinateY(sc));
        return coords;
    }

    private static Integer readCoordinateX(Scanner sc) {
        while (true) {
            System.out.print("Введите coordinates.x (Integer, > -881): ");
            String line = sc.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val > -881) {
                    return val;
                }
                System.out.println("Ошибка: значение должно быть больше -881.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    private static Long readCoordinateY(Scanner sc) {
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

    private static Float readMinimalPoint(Scanner sc) {
        while (true) {
            System.out.print("Введите minimalPoint (Float, > 0, пустая строка — null): ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                return null;
            }
            try {
                float val = Float.parseFloat(line);
                if (val > 0) {
                    return val;
                }
                System.out.println("Ошибка: значение должно быть больше 0.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число.");
            }
        }
    }

    private static long readMaximumPoint(Scanner sc) {
        while (true) {
            System.out.print("Введите maximumPoint (long, > 0): ");
            String line = sc.nextLine().trim();
            try {
                long val = Long.parseLong(line);
                if (val > 0) {
                    return val;
                }
                System.out.println("Ошибка: значение должно быть больше 0.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    private static String readDescription(Scanner sc) {
        System.out.print("Введите description (String, пустая строка — null): ");
        String line = sc.nextLine().trim();
        return line.isEmpty() ? null : line;
    }

    private static Difficulty readDifficulty(Scanner sc) {
        while (true) {
            System.out.println("Доступные значения Difficulty: EASY, HARD, VERY_HARD, INSANE");
            System.out.print("Введите difficulty (пустая строка — null): ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                return null;
            }
            try {
                return Difficulty.valueOf(line.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: нет такой константы. Попробуйте снова.");
            }
        }
    }

    private static Person readPerson(Scanner sc) {
        Person person = new Person();
        person.setName(readAuthorName(sc));
        person.setBirthday(readBirthday(sc));
        person.setLocation(readLocation(sc));
        return person;
    }

    private static String readAuthorName(Scanner sc) {
        while (true) {
            System.out.print("Введите author.name (String, не пустое): ");
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Ошибка: имя автора не может быть пустым.");
        }
    }

    private static Date readBirthday(Scanner sc) {
        while (true) {
            System.out.print("Введите author.birthday (дд.мм.гггг, пустая строка — null): ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                return null;
            }
            try {
                return DATE_FORMAT.parse(line);
            } catch (ParseException e) {
                System.out.println("Ошибка: неверный формат даты. Используйте дд.мм.гггг.");
            }
        }
    }

    private static Location readLocation(Scanner sc) {
        System.out.print("Хотите ввести author.location? (пустая строка — null, любой текст — да): ");
        String line = sc.nextLine().trim();
        if (line.isEmpty()) {
            return null;
        }
        Location loc = new Location();
        loc.setX(readLocationX(sc));
        loc.setY(readLocationY(sc));
        loc.setZ(readLocationZ(sc));
        return loc;
    }

    private static Integer readLocationX(Scanner sc) {
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

    private static float readLocationY(Scanner sc) {
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

    private static double readLocationZ(Scanner sc) {
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
