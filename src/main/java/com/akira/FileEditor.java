package com.akira;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import com.fasterxml.jackson.dataformat.xml.XmlMapper;


public class FileEditor {
    private static final String DATA_FILE_NAME = "data.xml";
    private static final XmlMapper xmlMapper = new XmlMapper();

    public static Hashtable<String, Object> getCollection() {

        try {
            File file = new File(DATA_FILE_NAME);

            try (InputStream inputStream = new FileInputStream(file);
                 InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                Hashtable<String, Object> result = xmlMapper.readValue(reader, Hashtable.class);
                System.out.println("Данные из XML: " + result);
                return result;
            } catch (FileNotFoundException e) {
                System.err.println("Файл не найден");
            }

            return new Hashtable<>();
        } catch (IOException e) {
            System.err.println("Ошибка работы с файлом: " + e.getMessage());
            return new Hashtable<>();
        }
    }

    public static void updateCollection(Hashtable<String, Object> coll) {
        try (FileWriter writer = new FileWriter(DATA_FILE_NAME, StandardCharsets.UTF_8)) {
            xmlMapper.writeValue(writer, coll);
        } catch (IOException e) {
            System.err.println("Ошибка записи в файл");
        }
    }

    public static Date getCollectionCreationTime() {
        try {
            BasicFileAttributes fileAttributes = Files.readAttributes(Path.of(DATA_FILE_NAME), BasicFileAttributes.class);
            return new Date(fileAttributes.creationTime().toMillis());
        } catch (IOException e) {
            System.err.println("Ошибка получения даты создания файла: " + e.getMessage());
            return null;
        }
    }

}