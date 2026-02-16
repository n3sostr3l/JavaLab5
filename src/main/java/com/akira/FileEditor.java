//package com.akira;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.Hashtable;
//
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//
//
//public class FileEditor {
//    public static void main(String[] args) {
//        XmlMapper xmlMapper = new XmlMapper();
//
//        try {
//            // Читаем XML файл прямо в Hashtable
//
//            File file = new File("data.xml");
//
//            try (InputStream inputStream = new FileInputStream(file);
//                 InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8")) {
//                Hashtable<String, Object> result = xmlMapper.readValue(reader, Hashtable.class);
//                System.out.println("Данные из XML: " + result);
//            } catch (FileNotFoundException e) {
//                System.err.println("Файл не найден");
//            }
//
//            // И наоборот: запись из Hashtable в XML
//
//        } catch (IOException e) {
//            System.err.println("Ошибка работы с файлом: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}