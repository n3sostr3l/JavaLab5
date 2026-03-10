package com.akira.general.commands;

import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.ThreadLocalRandom;

import com.akira.general.commands.interfaces.Command;
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
public class AddRandomCommand implements Command {
    private static final String[] LAB_NAMES = {
            "Математический Анализ", "Линейная Алгебра", "Промпт Инженерия", "Программирование на Java",
            "Программирование на Python", "Алгоритмы и структуры данных", "Дискретная Математика", "Основы Профессиональной Деятельности"
    };
    private static final String[] DESCRIPTIONS = {
            "Практическая работа", "Исследовательская работа", "Отчёт по теме", "Лабораторная работа", "Контрольная лабораторная", "Дополнительная работа"
    };

    @Override
    public Response execute(CollectionManager collectionManager) {
        Integer key = generateUniqueKey();
        LabWork randomLabWork = generateRandomLabWork();
        if (CollectionManager.insert(key, randomLabWork)) {
            return new Response("Случайная лабораторная работа добавлена. Ключ: " + key + ", id: " + randomLabWork.getId(), true);
        } else {
            return new Response("Ошибка при добавлении случайной работы.", false);
        }
    }

    @Override
    public String describe() {
        return "add_random : добавить случайно сгенерированную лабораторную работу";
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }

    private Integer generateUniqueKey() {
        Hashtable<Integer, LabWork> collection = CollectionManager.getCollection();
        Integer key;
        do {
            key = ThreadLocalRandom.current().nextInt(1, 100000);
        } while (collection.containsKey(key));
        return key;
    }

    private LabWork generateRandomLabWork() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
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
        long now = System.currentTimeMillis();
        person.setBirthday(new Date(now - random.nextLong(100L * 365 * 24 * 60 * 60 * 1000)));
        
        Location loc = new Location();
        loc.setX(random.nextInt(-880, 1001));
        loc.setY((float) random.nextDouble(-500.0, 500.0));
        loc.setZ(random.nextDouble(-500.0, 500.0));
        person.setLocation(loc);
        
        labWork.setAuthor(person);
        return labWork;
    }
}
