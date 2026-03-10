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

import com.akira.general.datas.LabWork;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;


/**
 * Класс для работы с файлом хранения коллекции.
 * <p>
 * Обеспечивает чтение и запись коллекции в XML-формат.
 * Использует {@link java.io.InputStreamReader} для чтения и
 * {@link java.io.FileWriter} для записи в соответствии с требованиями.
 * </p>
 */
public class FileEditor {
    /** Имя файла для хранения данных коллекции */
    private static final String DATA_FILE_NAME = resolveDataFileName();
    /** XML-маппер для сериализации и десериализации */
    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        xmlMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        xmlMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        xmlMapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
    }

    private static String resolveDataFileName() {
        String fromEnv = System.getenv("DATA_FILE_NAME");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }

        String fromDotEnvFile = readFromDotEnvFile();
        if (fromDotEnvFile != null && !fromDotEnvFile.isBlank()) {
            return fromDotEnvFile;
        }

        String fromDotEnvResource = readFromDotEnvResource();
        if (fromDotEnvResource != null && !fromDotEnvResource.isBlank()) {
            return fromDotEnvResource;
        }

        return "data.xml";
    }

    private static String readFromDotEnvFile() {
        Path dotEnvPath = Path.of(".env");
        if (Files.notExists(dotEnvPath)) {
            return null;
        }

        try (BufferedReader bufferedReader = Files.newBufferedReader(dotEnvPath, StandardCharsets.UTF_8)) {
            return readDataFileNameFromReader(bufferedReader);
        } catch (IOException e) {
            return null;
        }
    }

    private static String readFromDotEnvResource() {
        try (InputStream inputStream = FileEditor.class.getClassLoader().getResourceAsStream(".env")) {
            if (inputStream == null) {
                return null;
            }
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return readDataFileNameFromReader(bufferedReader);
            }
        } catch (IOException e) {
            return null;
        }
    }

    private static String readDataFileNameFromReader(BufferedReader reader) throws IOException {
        Properties properties = new Properties();
        properties.load(reader);
        String value = properties.getProperty("DATA_FILE_NAME");
        return value == null ? null : value.trim();
    }

    /**
     * Читает коллекцию из XML-файла.
     * <p>
     * При ошибке чтения возвращает пустую коллекцию.
     * </p>
     *
     * @return коллекция лабораторных работ, загруженная из файла
     */
    public static Hashtable<Integer, LabWork> getCollection() {
        Path dataPath = Path.of(DATA_FILE_NAME);
        if (Files.notExists(dataPath)) {
            System.err.println("Файл не найден");
            return new Hashtable<>();
        }

        try (InputStream inputStream = Files.newInputStream(dataPath);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            Hashtable<String, LabWork> raw = xmlMapper.readValue(reader,
                    xmlMapper.getTypeFactory().constructMapType(Hashtable.class, String.class, LabWork.class));
            return unwrapKeys(raw);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Ошибка работы с файлом: " + e.getMessage());
            return new Hashtable<>();
        }
    }

    private static Hashtable<Integer, LabWork> unwrapKeys(Hashtable<String, LabWork> raw) {
        Hashtable<Integer, LabWork> result = new Hashtable<>();
        for (var e : raw.entrySet()) {
            String keyStr = e.getKey().startsWith("k_") ? e.getKey().substring(2) : e.getKey();
            result.put(Integer.parseInt(keyStr), e.getValue());
        }
        return result;
    }

    /**
     * Сохраняет коллекцию в XML-файл.
     * <p>
     * Ключи коллекции преобразуются в строковый формат с префиксом "k_"
     * для корректной сериализации в XML.
     * </p>
     *
     * @param coll коллекция для сохранения
     */
    public static void saveCollection(Hashtable<Integer, LabWork> coll) {
        try (FileWriter writer = new FileWriter(DATA_FILE_NAME, StandardCharsets.UTF_8)) {
            Hashtable<String, LabWork> wrapped = new Hashtable<>();
            coll.forEach((key, value) -> wrapped.put("k_" + key, value));
            xmlMapper.writerWithDefaultPrettyPrinter().writeValue(writer, wrapped);
        } catch (IOException e) {
            System.err.println("Ошибка записи в файл");
        }
    }

    /**
     * Возвращает дату создания файла коллекции.
     *
     * @return дата создания файла или null при ошибке
     */
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