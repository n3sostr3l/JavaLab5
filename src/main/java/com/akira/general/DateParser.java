package com.akira.general;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Утилитный класс для парсинга и валидации дат.
 */
public class DateParser{

    // Константы для границ года
    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2015;

    // Формат даты по умолчанию (dd.mm.yyyy)
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Парсит строку в LocalDate с проверкой диапазона.
     *
     * @param dateStr Строка с датой
     * @return Объект Date
     * @throws IllegalArgumentException Если дата некорректна или вне диапазона
     */
    public static LocalDate parseAndValidate(String dateStr) {
        LocalDate date;

        try {
            date = LocalDate.parse(dateStr, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты или несуществующая дата: " + dateStr);
        }

        int year = date.getYear();
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new IllegalArgumentException(
                    String.format("Год %d вне допустимого диапазона [%d - %d]", year, MIN_YEAR, MAX_YEAR)
            );
        }

        return date;
    }
}