package com.akira.integration;

import com.akira.client.NetworkManager;
import com.akira.general.datas.*;
import com.akira.general.network.Request;
import com.akira.general.network.Response;
import com.akira.server.ServerManager;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.FileWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.*;

public class ScriptRecursionTest {
    private static final int TEST_PORT = 54324;
    private static ExecutorService serverExecutor;
    private static final String S1 = "script1.txt";
    private static final String S2 = "script2.txt";

    @BeforeAll
    public static void setup() throws Exception {
        System.setProperty("DATA_FILE_NAME", "test_script_coll.xml");
        serverExecutor = Executors.newSingleThreadExecutor();
        serverExecutor.submit(() -> new ServerManager(TEST_PORT).start());
        Thread.sleep(1000);
    }

    @AfterAll
    public static void teardown() {
        serverExecutor.shutdownNow();
        new File(S1).delete();
        new File(S2).delete();
        new File("test_script_coll.xml").delete();
    }

    @Test
    public void testRecursionPrevention() throws Exception {
        // Создаем рекурсивные скрипты
        // script1: execute_script script2.txt
        // script2: execute_script script1.txt
        try (FileWriter w1 = new FileWriter(S1)) {
            w1.write("execute_script " + S2 + "\n");
        }
        try (FileWriter w2 = new FileWriter(S2)) {
            w2.write("execute_script " + S1 + "\n");
        }

        // Перехватываем вывод в консоль
        PrintStream oldOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        try {
            // Имитируем работу клиента (Main.executeScript доступен через рефлексию или просто вызовем Main.main с файлом)
            // Но в нашей реализации Main.main читает System.in. 
            // Мы можем использовать рефлексию для вызова приватного метода executeScript.
            java.lang.reflect.Method method = com.akira.client.Main.class.getDeclaredMethod("executeScript", String.class);
            method.setAccessible(true);
            
            // Нам нужен инициализированный NetworkManager и LabWorkReader в Main
            // Для теста просто инициализируем их через рефлексию
            com.akira.client.NetworkManager nm = new com.akira.client.NetworkManager("localhost", TEST_PORT);
            java.lang.reflect.Field nmField = com.akira.client.Main.class.getDeclaredField("networkManager");
            nmField.setAccessible(true);
            nmField.set(null, nm);
            
            method.invoke(null, S1);

            String output = baos.toString();
            assertTrue(output.contains("Обнаружена рекурсия"), "Должно быть сообщение о рекурсии");
        } finally {
            System.setOut(oldOut);
        }
    }
}
