package com.akira.server;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.akira.general.datas.LabWork;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Класс для работы с файлами хранения коллекции (основным и сессионными).
 */
public class FileEditor {
    /** Имя файла для сохранения сессии по умолчанию. */
    public static String DATA_FILE_NAME = "last_saved_session.xml";
    private static final XmlMapper xmlMapper = new XmlMapper();


    static {
        xmlMapper.registerModule(new JavaTimeModule());

        xmlMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        xmlMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        xmlMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        xmlMapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
    }

    /**
     * Возвращает имя основного файла данных.
     * @return имя файла
     */
    public static String getDataFileName() { return DATA_FILE_NAME; }
    /**
     * Устанавливает имя основного файла данных.
     * @param name имя файла
     */
    public static void setDataFileName(String name) { DATA_FILE_NAME = name; }
    /**
     * Проверяет существование файла.
     * @param fileName имя файла
     * @return true, если файл существует
     */
    public static boolean exists(String fileName) { return Files.exists(Path.of(fileName)); }

    
    /**
     * Загружает коллекцию из XML-файла.
     * @param fileName имя файла
     * @return хеш-таблица объектов
     */
    public static Hashtable<Integer, LabWork> loadFromFile(String fileName) {
        Path path = Path.of(fileName);
        if (Files.notExists(path)) return new Hashtable<>();
        try (InputStream is = Files.newInputStream(path);
             InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            Hashtable<String, LabWork> raw = xmlMapper.readValue(reader,
                    xmlMapper.getTypeFactory().constructMapType(Hashtable.class, String.class, LabWork.class));
            Hashtable<Integer, LabWork> result = new Hashtable<>();
            for (var e : raw.entrySet()) {
                String keyStr = e.getKey().startsWith("k_") ? e.getKey().substring(2) : e.getKey();
                result.put(Integer.parseInt(keyStr), e.getValue());
            }
            return result;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Ошибка чтения файла " + fileName + ": " + e.getMessage());
            return new Hashtable<>();
        }
    }

    /**
     * Сохраняет коллекцию в XML-файл.
     * @param fileName имя файла
     * @param coll коллекция объектов
     * @return true, если сохранение прошло успешно
     */
    public static boolean saveToFile(String fileName, Hashtable<Integer, LabWork> coll) {
        try (FileWriter writer = new FileWriter(fileName, StandardCharsets.UTF_8)) {
            Hashtable<String, LabWork> wrapped = new Hashtable<>();
            coll.forEach((key, value) -> wrapped.put("k_" + key, value));
            xmlMapper.writerWithDefaultPrettyPrinter().writeValue(writer, wrapped);
            return true;
        } catch (IOException e) {
            System.err.println("Ошибка записи в файл " + fileName + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Возвращает коллекцию из основного файла данных.
     * @return хеш-таблица объектов
     */
    public static Hashtable<Integer, LabWork> getCollection() { return loadFromFile(DATA_FILE_NAME); }
    /**
     * Сохраняет коллекцию в основной файл данных.
     * @param coll коллекция объектов
     * @return true, если сохранение прошло успешно
     */
    public static boolean saveCollection(Hashtable<Integer, LabWork> coll) { return saveToFile(DATA_FILE_NAME, coll); }
    /**
     * Загружает сессионную коллекцию из указанного файла.
     * @param fileName имя файла
     * @return хеш-таблица объектов
     */
    public static Hashtable<Integer, LabWork> getSessionCollection(String fileName) { return loadFromFile(fileName); }

    /**
     * Возвращает время создания основного файла данных.
     * @return дата и время создания или null
     */
    public static Date getCollectionCreationTime() {
        try {
            BasicFileAttributes attrs = Files.readAttributes(Path.of(DATA_FILE_NAME), BasicFileAttributes.class);
            return new Date(attrs.creationTime().toMillis());
        } catch (IOException e) {
            return null;
        }
    }
}