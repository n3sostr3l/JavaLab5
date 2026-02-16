package com.akira;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;


public class FileEditor{
    XmlMapper xmlMapper = new XmlMapper();
    public Hashtable<String, Object> openfile() throws IOException, FileNotFoundException{
        try {
            // Читаем XML файл прямо в Hashtable

            File file = new File("data.xml");

            try (InputStream inputStream = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8")) {
                    Hashtable<String, Object> result = xmlMapper.readValue(reader, Hashtable.class);
                return result;
            }
            catch (FileNotFoundException e){
                System.err.println("Файл не найден");
                throw e;
            }
            

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
    public void savefile(Hashtable<String, Object> table) {
        try {
            // Создаем файл для записи
            File file = new File("data.xml");
            
            // Сериализуем Hashtable в XML и записываем в файл
            xmlMapper.writeValue(file, table);
        } catch (IOException e) {
            System.err.println("Ошибка записи в файл: " + e.getMessage());
            e.printStackTrace();
        }
    }
}