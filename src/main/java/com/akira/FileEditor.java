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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;


public class FileEditor {
    private static final String DATA_FILE_NAME = "data.xml";
    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        xmlMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        xmlMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        xmlMapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
    }

    public static Hashtable<String, LabWork> getCollection() {

        try {
            File file = new File(DATA_FILE_NAME);

            try (InputStream inputStream = new FileInputStream(file);
                 InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                Hashtable<String, LabWork> raw = xmlMapper.readValue(reader,
                        xmlMapper.getTypeFactory().constructMapType(Hashtable.class, String.class, LabWork.class));
                Hashtable<String, LabWork> result = new Hashtable<>();
                for (var e : raw.entrySet()) {
                    result.put(e.getKey().startsWith("k_") ? e.getKey().substring(2) : e.getKey(), e.getValue());
                }
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

    public static void saveCollection(Hashtable<String, LabWork> coll) {
        try (FileWriter writer = new FileWriter(DATA_FILE_NAME, StandardCharsets.UTF_8)) {
            Hashtable<String, LabWork> wrapped = new Hashtable<>();
            for (var e : coll.entrySet()) {
                wrapped.put("k_" + e.getKey(), e.getValue());
            }
            xmlMapper.writerWithDefaultPrettyPrinter().writeValue(writer, wrapped);
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