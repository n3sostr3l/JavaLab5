package com.akira.server.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Date;
import java.time.LocalDate;
import java.util.Hashtable;
import java.util.concurrent.ThreadLocalRandom;

import com.akira.server.commands.interfaces.Modable;
import com.akira.general.datas.Coordinates;
import com.akira.general.datas.Difficulty;
import com.akira.general.datas.LabWork;
import com.akira.general.datas.Location;
import com.akira.general.datas.Person;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;

/**
 * Команда генерации случайной лабораторной работы.
 */
public class AddRandomCommand implements Modable {
    private int number;
    private static final String[] LAB_NAMES = {
            "Математический Анализ", "Линейная Алгебра", "Промпт Инженерия", "Программирование на Java",
            "Программирование на Python", "Алгоритмы и структуры данных", "Дискретная Математика", "Основы Профессиональной Деятельности"
    };
    private static final String[] DESCRIPTIONS = {
            "Практическая работа", "Исследовательская работа", "Отчёт по теме", "Лабораторная работа", "Контрольная лабораторная", "Дополнительная работа"
    };

    @Override
    public Response execute(CollectionManager collectionManager) {
        HashSet<Integer> keys = new HashSet<>();
        ArrayList<LabWork> randomLabWork = generateRandomLabWork();
        for (LabWork labWork : randomLabWork) {
            Integer key = generateUniqueKey();
            while (keys.contains(key)) {
                key = generateUniqueKey();
            }
            keys.add(key);
            CollectionManager.insert(key, labWork);
        }
        return new Response("Случайные лабораторные работы добавлены. Ключи: " + keys.toString(), true);
    }

    @Override
    public String describe() {
        return "add_random {number}: добавить {number} случайно сгенерированных лабораторных работ";
    }

    @Override
    public int numberArgsRequired() {
        return 1;
    }
    
    @Override
    public void setArguments(ArrayList<String> args) {
        this.number = Integer.parseInt(args.get(0));
    }

    private Integer generateUniqueKey() {
        Hashtable<Integer, LabWork> collection = CollectionManager.getCollection();
        Integer key;
        do {
            key = ThreadLocalRandom.current().nextInt(1, 100000);
        } while (collection.containsKey(key));
        return key;
    }

    private ArrayList<LabWork> generateRandomLabWork() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        ArrayList<LabWork> labWorks = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            LabWork labWork = new LabWork();
            labWork.setCreationDate(new Date());
            labWork.setName(LAB_NAMES[random.nextInt(LAB_NAMES.length)] + " " + random.nextInt(1, 101));
            
            Coordinates coords = new Coordinates();
            coords.setX(random.nextInt(-880, 1000));
            coords.setY(random.nextLong(-1000, 1000));
            labWork.setCoordinates(coords);
            
            labWork.setMinimalPoint(random.nextFloat(1.0f, 51.0f));
            labWork.setMaximumPoint(random.nextLong(51, 201));
            labWork.setDescription(DESCRIPTIONS[random.nextInt(DESCRIPTIONS.length)]);
            
            Difficulty[] values = Difficulty.values();
            labWork.setDifficulty(values[random.nextInt(values.length)]);
            
            Person person = new Person();
            person.setName("Author_" + random.nextInt(1, 10000));

            person.setBirthday(LocalDate.of(ThreadLocalRandom.current().nextInt(1999,2010), ThreadLocalRandom.current().nextInt(1,12), ThreadLocalRandom.current().nextInt(1,28)));
            
            Location loc = new Location();
            loc.setX(random.nextInt(-880, 1001));
            loc.setY((float) random.nextDouble(-500.0, 500.0));
            loc.setZ(random.nextDouble(-500.0, 500.0));
            person.setLocation(loc);
            
            labWork.setAuthor(person);
            labWorks.add(labWork);
        }
        return labWorks;
    }
}
