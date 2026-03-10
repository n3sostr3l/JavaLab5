package com.akira.general.network;

import com.akira.general.datas.*;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import static org.junit.jupiter.api.Assertions.*;

public class SerializationTest {

    @Test
    public void testRequestFullSerialization() throws IOException, ClassNotFoundException {
        // 1. Создание сложного объекта LabWork
        LabWork labWork = new LabWork();
        labWork.setName("TestLab");
        labWork.setCreationDate(new Date());
        labWork.setMaximumPoint(100L);
        
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
        ArrayList<String> args = new ArrayList<>();
        args.add("arg1");
        args.add("arg2");
        Request originalRequest = new Request("add", args, labWork);

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
        
        LabWork restoredLab = restoredRequest.getObjectArgument();
        assertNotNull(restoredLab);
        assertEquals("TestLab", restoredLab.getName());
        assertEquals(100L, restoredLab.getMaximumPoint());
        assertEquals(Difficulty.INSANE, restoredLab.getDifficulty());
        assertEquals("AuthorName", restoredLab.getAuthor().getName());
        assertEquals(10, restoredLab.getCoordinates().getX());
    }

    @Test
    public void testResponseWithCollectionSerialization() throws IOException, ClassNotFoundException {
        Hashtable<Integer, LabWork> collection = new Hashtable<>();
        LabWork lab1 = new LabWork();
        lab1.setName("Lab1");
        collection.put(1, lab1);

        Response originalResponse = new Response("Successfully shown", true, collection);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(originalResponse);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        Response restoredResponse = (Response) ois.readObject();

        assertEquals(originalResponse.getMessage(), restoredResponse.getMessage());
        assertEquals(originalResponse.isSuccess(), restoredResponse.isSuccess());
        assertNotNull(restoredResponse.getCollection());
        assertEquals(1, restoredResponse.getCollection().size());
        assertEquals("Lab1", restoredResponse.getCollection().get(1).getName());
    }
}
