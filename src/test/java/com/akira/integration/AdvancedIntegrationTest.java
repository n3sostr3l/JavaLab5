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
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdvancedIntegrationTest {
    private static final int TEST_PORT = 54326;
    private static ExecutorService serverExecutor;
    private static NetworkManager client;
    private static final String TEST_FILE = "test_adv_collection_3.xml";
    private static final String SCRIPT_FILE = "test_script_exec_2.txt";

    @BeforeAll
    public static void setup() throws Exception {
        new File(TEST_FILE).delete();
        try (FileWriter writer = new FileWriter(TEST_FILE)) {
            writer.write("<Hashtable/>");
        }
        System.setProperty("DATA_FILE_NAME", TEST_FILE);
        
        serverExecutor = Executors.newSingleThreadExecutor();
        serverExecutor.submit(() -> new ServerManager(TEST_PORT).start());
        
        Thread.sleep(2000);
        client = new NetworkManager("localhost", TEST_PORT);
        assertTrue(client.connect());

        java.lang.reflect.Field nmField = com.akira.client.Main.class.getDeclaredField("networkManager");
        nmField.setAccessible(true);
        nmField.set(null, client);
        java.lang.reflect.Field readerField = com.akira.client.Main.class.getDeclaredField("labWorkReader");
        readerField.setAccessible(true);
        readerField.set(null, new com.akira.general.LabWorkReader(new java.util.Scanner(System.in)));
    }

    @AfterAll
    public static void teardown() {
        client.close();
        serverExecutor.shutdownNow();
        new File(TEST_FILE).delete();
        new File(SCRIPT_FILE).delete();
    }

    @Test
    @Order(1)
    public void testFullCommandCycle() {
        client.sendAndReceive(new Request("clear"));
        Response res = client.sendAndReceive(new Request("insert", list("10"), createTestLab("Lab1", 100L)));
        assertTrue(res.isSuccess());

        Response showRes = client.sendAndReceive(new Request("show"));
        long id = showRes.getCollection().get(10).getId();
        Response updateRes = client.sendAndReceive(new Request("update", list(String.valueOf(id)), createTestLab("UpdatedLab", 200L)));
        assertTrue(updateRes.isSuccess());
    }

    @Test
    @Order(2)
    public void testStreamApiCommands() {
        client.sendAndReceive(new Request("clear"));
        client.sendAndReceive(new Request("insert", list("1"), createTestLab("A", 100L)));
        client.sendAndReceive(new Request("insert", list("2"), createTestLab("B", 200L)));
        client.sendAndReceive(new Request("insert", list("3"), createTestLab("C", 200L)));

        Response groupRes = client.sendAndReceive(new Request("group_counting_by_maximum_point"));
        String msg = groupRes.getMessage();
        // В Map может быть "200: 2" или "200=2"
        assertTrue(msg.contains("200") && msg.contains("2"), "Msg: " + msg);
    }

    @Test
    @Order(3)
    public void testExecuteScript() throws Exception {
        try (FileWriter w = new FileWriter(SCRIPT_FILE)) {
            w.write("clear\n");
            w.write("insert 55\n");
            w.write("TestLabFromScript\n"); // name
            w.write("10\n");               // x
            w.write("20\n");               // y
            w.write("1.5\n");              // minPoint
            w.write("100\n");              // maxPoint
            w.write("Desc\n");             // description
            w.write("EASY\n");             // difficulty
            w.write("AuthorName\n");       // author name
            w.write("\n");                 // birthday (null)
            w.write("нет\n");              // location (да/нет)
            w.write("show\n");
        }

        java.lang.reflect.Method method = com.akira.client.Main.class.getDeclaredMethod("executeScript", String.class);
        method.setAccessible(true);
        method.invoke(null, SCRIPT_FILE);

        Response showRes = client.sendAndReceive(new Request("show"));
        assertTrue(showRes.getMessage().contains("TestLabFromScript"));
        assertTrue(showRes.getCollection().containsKey(55));
    }

    private LabWork createTestLab(String name, long maxPoint) {
        LabWork lab = new LabWork();
        lab.setName(name);
        lab.setMaximumPoint(maxPoint);
        lab.setCoordinates(new Coordinates());
        lab.getCoordinates().setX(1);
        lab.getCoordinates().setY(1L);
        Person author = new Person();
        author.setName("TestAuthor");
        lab.setAuthor(author);
        return lab;
    }

    private ArrayList<String> list(String... items) {
        return new ArrayList<>(Arrays.asList(items));
    }
}
