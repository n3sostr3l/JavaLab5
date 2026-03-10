package com.akira.integration;

import com.akira.client.NetworkManager;
import com.akira.general.datas.*;
import com.akira.general.network.Request;
import com.akira.general.network.Response;
import com.akira.server.ServerManager;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FullIntegrationTest {
    private static final int TEST_PORT = 54322; // Сменим порт на всякий случай
    private static ExecutorService serverExecutor;
    private static NetworkManager client;
    private static final String TEST_FILE = "test_collection_int.xml";

    @BeforeAll
    public static void setup() {
        try {
            // Создаем пустой XML файл для тестов
            File f = new File(TEST_FILE);
            try (FileWriter writer = new FileWriter(f)) {
                writer.write("<Hashtable/>");
            }
            
            System.setProperty("DATA_FILE_NAME", TEST_FILE);
            
            serverExecutor = Executors.newSingleThreadExecutor();
            serverExecutor.submit(() -> {
                try {
                    ServerManager server = new ServerManager(TEST_PORT);
                    server.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
            Thread.sleep(2000);
            
            client = new NetworkManager("localhost", TEST_PORT);
            if (!client.connect()) {
                Thread.sleep(2000);
                if (!client.connect()) {
                    throw new RuntimeException("Failed to connect to server");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public static void teardown() {
        try {
            if (client != null) client.close();
            if (serverExecutor != null) serverExecutor.shutdownNow();
            new File(TEST_FILE).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void testInfoCommand() {
        Request request = new Request("info");
        Response response = client.sendAndReceive(request);
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    @Test
    @Order(2)
    public void testInsertAndShow() {
        LabWork lab = new LabWork();
        lab.setName("IntegrationLab");
        lab.setMaximumPoint(500L);
        lab.setAuthor(new Person());
        lab.getAuthor().setName("TestAuthor");
        lab.setCoordinates(new Coordinates());
        lab.getCoordinates().setX(1);
        lab.getCoordinates().setY(1L);
        
        ArrayList<String> args = new ArrayList<>();
        args.add("1");
        
        Response res = client.sendAndReceive(new Request("insert", args, lab));
        assertTrue(res.isSuccess());

        Response showRes = client.sendAndReceive(new Request("show"));
        assertTrue(showRes.getMessage().contains("IntegrationLab"));
    }
}
