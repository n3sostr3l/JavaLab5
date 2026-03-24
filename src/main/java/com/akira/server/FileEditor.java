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
    private static String DATA_FILE_NAME = resolveDataFileName();
    /** XML-маппер для сериализации и десериализации */
    private static final XmlMapper xmlMapper = new XmlMapper();

    public static void setDataFileName(String name) {
        DATA_FILE_NAME = name;
    }

    public static String getDataFileName() {
        return DATA_FILE_NAME;
    }

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

        String fromProp = System.getProperty("DATA_FILE_NAME");
        if (fromProp != null && !fromProp.isBlank()) {
            return fromProp;
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

    private static String getBackupFileName() {
        return DATA_FILE_NAME + ".bak";
    }

    /**
     * Сохраняет коллекцию в резервный файл.
     */
    public static void saveBackup(Hashtable<Integer, LabWork> coll) {
        try (FileWriter writer = new FileWriter(getBackupFileName(), StandardCharsets.UTF_8)) {
            Hashtable<String, LabWork> wrapped = new Hashtable<>();
            coll.forEach((key, value) -> wrapped.put("k_" + key, value));
            xmlMapper.writerWithDefaultPrettyPrinter().writeValue(writer, wrapped);
        } catch (IOException ignored) {}
    }

    /**
     * Удаляет резервный файл.
     */
    public static void deleteBackup() {
        try {
            Files.deleteIfExists(Path.of(getBackupFileName()));
        } catch (IOException ignored) {}
    }

    /**
     * Проверяет наличие резервной копии.
     */
    public static boolean hasBackup() {
        return Files.exists(Path.of(getBackupFileName()));
    }

    /**
     * Загружает коллекцию из резервной копии.
     */
    public static Hashtable<Integer, LabWork> getBackupCollection() {
        String originalFile = DATA_FILE_NAME;
        // Временно подменяем имя для использования существующего метода загрузки
        // Но лучше просто вызвать чтение с параметром. 
        // Однако для минимальных правок:
        return loadFromFile(getBackupFileName());
    }

    private static Hashtable<Integer, LabWork> loadFromFile(String fileName) {
        Path dataPath = Path.of(fileName);
        if (Files.notExists(dataPath)) return new Hashtable<>();
        try (InputStream inputStream = Files.newInputStream(dataPath);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            Hashtable<String, LabWork> raw = xmlMapper.readValue(reader,
                    xmlMapper.getTypeFactory().constructMapType(Hashtable.class, String.class, LabWork.class));
            return unwrapKeys(raw);
        } catch (IOException | NumberFormatException e) {
            return new Hashtable<>();
        }
    }

    /**
     * Сохраняет коллекцию в XML-файл.
     * @param coll коллекция
     * @return true если успешно
     */
    public static boolean saveCollection(Hashtable<Integer, LabWork> coll) {
        try (FileWriter writer = new FileWriter(DATA_FILE_NAME, StandardCharsets.UTF_8)) {
            Hashtable<String, LabWork> wrapped = new Hashtable<>();
            coll.forEach((key, value) -> wrapped.put("k_" + key, value));
            xmlMapper.writerWithDefaultPrettyPrinter().writeValue(writer, wrapped);
            return true;
        } catch (IOException e) {
            System.err.println("Ошибка записи в файл");
            return false;
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