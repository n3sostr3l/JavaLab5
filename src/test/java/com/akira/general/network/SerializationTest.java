package com.akira.general.network;

import com.akira.general.datas.*;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

public class SerializationTest {

    @Test
    public void testRequestFullSerialization() throws IOException, ClassNotFoundException {
        // 1. Создание сложного объекта LabWork
        LabWork labWork = new LabWork();
        labWork.setName("TestLab");
        labWork.setCreationDate(new Date());
        labWork.setMaximumPoint(100);
        
        Coordinates coords = new Coordinates();
        coords.setX(10);
        coords.setY(20L);
        labWork.setCoordinates(coords);
        
        Person author = new Person();
        author.setName("AuthorName");
        Location loc = new Location();
        loc.setX(1); loc.setY(2.0f); loc.setZ(3.0);
        author.setLocation(loc);
        labWork.setAuthor(author);
        
        labWork.setDifficulty(Difficulty.INSANE);

        // 2. Создание Request
        Request originalRequest = new Request("add", "some_args", labWork);

        // 3. Сериализация в байты
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(originalRequest);
        byte[] bytes = baos.toByteArray();

        // 4. Десериализация из байтов
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Request restoredRequest = (Request) ois.readObject();

        // 5. Проверки (Assertions)
        assertNotNull(restoredRequest);
        assertEquals(originalRequest.getCommandName(), restoredRequest.getCommandName());
        assertEquals(originalRequest.getArgs(), restoredRequest.getArgs());
        
        LabWork restoredLab = restoredRequest.getObjectArg();
        assertNotNull(restoredLab);
        assertEquals("TestLab", restoredLab.getName());
        assertEquals(100, restoredLab.getMaximumPoint());
        assertEquals(Difficulty.INSANE, restoredLab.getDifficulty());
        assertEquals("AuthorName", restoredLab.getAuthor().getName());
        assertEquals(10, restoredLab.getCoordinates().toString().contains("10") ? 10 : 0); // Проверка через toString или геттеры
    }

    @Test
    public void testResponseSerialization() throws IOException, ClassNotFoundException {
        Response originalResponse = new Response("Successfully added", true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(originalResponse);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        Response restoredResponse = (Response) ois.readObject();

        assertEquals(originalResponse.getMessage(), restoredResponse.getMessage());
        assertEquals(originalResponse.isSuccess(), restoredResponse.isSuccess());
    }
}
