package com.akira;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import com.akira.commands.CommandInvoker;

/**
 * Класс для интерактивного чтения данных лабораторной работы из консоли.
 * <p>
 * Предоставляет методы для поэтапного ввода всех полей объекта {@link LabWork}
 * с валидацией пользовательского ввода.
 * </p>
 */
public class LabWorkReader {
    /** Формат для парсинга даты рождения */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    static {
        DATE_FORMAT.setLenient(false);
    }

    /**
     * Читает данные для создания нового объекта LabWork из консоли.
     * <p>
     * Метод запрашивает у пользователя ввод всех необходимых полей
     * с постраничной валидацией.
     * </p>
     *
     * @return новый объект LabWork с данными, введёнными пользователем
     */
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

    /**
     * Читает название лабораторной работы.
     *
     * @param sc сканер для чтения ввода
     * @return непустая строка с названием
     */
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

    /**
     * Читает координаты лабораторной работы.
     *
     * @param sc сканер для чтения ввода
     * @return объект Coordinates с введёнными значениями
     */
    private static Coordinates readCoordinates(Scanner sc) {
        Coordinates coords = new Coordinates();
        coords.setX(readCoordinateX(sc));
        coords.setY(readCoordinateY(sc));
        return coords;
    }

    /**
     * Читает координату X.
     *
     * @param sc сканер для чтения ввода
     * @return целое число больше -881
     */
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

    /**
     * Читает координату Y.
     *
     * @param sc сканер для чтения ввода
     * @return целое число (Long)
     */
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

    /**
     * Читает минимальный балл.
     *
     * @param sc сканер для чтения ввода
     * @return число больше 0 или null
     */
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

    /**
     * Читает максимальный балл.
     *
     * @param sc сканер для чтения ввода
     * @return число больше 0
     */
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

    /**
     * Читает описание лабораторной работы.
     *
     * @param sc сканер для чтения ввода
     * @return строка описания или null
     */
    private static String readDescription(Scanner sc) {
        System.out.print("Введите description (String, пустая строка — null): ");
        String line = sc.nextLine().trim();
        return line.isEmpty() ? null : line;
    }

    /**
     * Читает уровень сложности лабораторной работы.
     *
     * @param sc сканер для чтения ввода
     * @return значение Difficulty или null
     */
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

    /**
     * Читает данные автора лабораторной работы.
     *
     * @param sc сканер для чтения ввода
     * @return объект Person с данными автора
     */
    private static Person readPerson(Scanner sc) {
        Person person = new Person();
        person.setName(readAuthorName(sc));
        person.setBirthday(readBirthday(sc));
        person.setLocation(readLocation(sc));
        return person;
    }

    /**
     * Читает имя автора.
     *
     * @param sc сканер для чтения ввода
     * @return непустая строка с именем автора
     */
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

    /**
     * Читает дату рождения автора.
     *
     * @param sc сканер для чтения ввода
     * @return дата рождения или null
     */
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

    /**
     * Читает местоположение автора.
     * <p>
     * Спрашивает пользователя, хочет ли он ввести местоположение.
     * При согласии запрашивает координаты X, Y, Z.
     * </p>
     *
     * @param sc сканер для чтения ввода
     * @return объект Location или null, если пользователь отказался от ввода
     */
    private static Location readLocation(Scanner sc) {
        System.out.print("Хотите ввести author.location? (да/нет): ");
        String line = sc.nextLine().trim();
        if (!line.equalsIgnoreCase("да")) {
            return null;
        }
        Location loc = new Location();
        loc.setX(readLocationX(sc));
        loc.setY(readLocationY(sc));
        loc.setZ(readLocationZ(sc));
        return loc;
    }

    /**
     * Читает координату X местоположения.
     *
     * @param sc сканер для чтения ввода
     * @return целое число — координата X
     */
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

    /**
     * Читает координату Y местоположения.
     *
     * @param sc сканер для чтения ввода
     * @return число с плавающей точкой — координата Y
     */
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

    /**
     * Читает координату Z местоположения.
     *
     * @param sc сканер для чтения ввода
     * @return число с плавающей точкой двойной точности — координата Z
     */
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
