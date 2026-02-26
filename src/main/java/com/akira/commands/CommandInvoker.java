package com.akira.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Класс для управления и выполнения команд приложения.
 * <p>
 * Служит центральным диспетчером команд, предоставляя возможность
 * их регистрации, вызова и выполнения. Поддерживает интерактивный режим
 * работы с консолью и выполнение команд из скриптов.
 * </p>
 * <p>
 * При запуске автоматически регистрирует все доступные команды
 * и предоставляет интерактивный интерфейс для их вызова.
 * </p>
 */
public class CommandInvoker {
    /** Сканер для чтения пользовательского ввода из консоли */
    private static Scanner sc = new Scanner(System.in);
    private static HashMap<String, Command> commands = new HashMap<String, Command>();

    /**
     * Создаёт новый invoking и регистрирует все доступные команды.
     * <p>
     * В конструкторе выполняется регистрация следующих команд:
     * <ul>
     *   <li>help — вывод справки по командам</li>
     *   <li>clear — очистка коллекции</li>
     *   <li>info — информация о коллекции</li>
     *   <li>show — отображение всех элементов</li>
     *   <li>exit — завершение работы</li>
     *   <li>execute_file — выполнение команд из файла</li>
     *   <li>insert — добавление элемента</li>
     *   <li>update — обновление элемента по id</li>
     *   <li>print_unique_author — вывод уникальных авторов</li>
     *   <li>save — сохранение коллекции в файл</li>
     *   <li>remove_key — удаление элемента по ключу</li>
     *   <li>print_field_descending_difficulty — вывод сложности по убыванию</li>
     *   <li>group_counting_by_maximum_point — группировка по максимальному баллу</li>
     *   <li>remove_lower_key — удаление элементов с меньшими ключами</li>
     *   <li>replace_if_greater — замена при большем значении</li>
     *   <li>replace_if_lower — замена при меньшем значении</li>
     * </ul>
     */
    public CommandInvoker() {
        commands.put("help", new HelpCommand()); //done 1
        commands.put("clear", new ClearCommand()); //done 1
        commands.put("info", new InfoCommand()); // done 1
        commands.put("show", new ShowCommand()); // done 1
        commands.put("exit", new ExitCommand()); // done 1
        commands.put("execute_file", new ExecuteCommand()); // done
        commands.put("insert", new InsertCommand()); // done 1
        commands.put("update", new UpdateCommand()); // done 1
        commands.put("print_unique_author", new UniqueAuthorCommand()); // done + 1
        commands.put("save", new SaveCommand()); // done 1
        commands.put("remove_key", new RemoveCommand()); // done
        commands.put("print_field_descending_difficulty", new PrintFieldDescendingDifficultyCommand()); // done
        commands.put("group_counting_by_maximum_point", new GroupCountingByMaximumPointCommand()); // done
        commands.put("remove_lower_key", new RemoveLowerElementsCommand()); // done
        commands.put("replace_if_greater", new ReplaceGreatestCommand()); // done
        commands.put("replace_if_lower", new ReplaceLowestCommand()); // done
        commands.put("add_random", new AddRandomCommand()); // done

    }

    /**
     * Запускает интерактивный режим работы приложения.
     * <p>
     * Читает команды из стандартного ввода построчно и передаёт их
     * на выполнение до завершения работы программы.
     * </p>
     */
    public void run() {
        while (sc.hasNext()) {
            String line = sc.nextLine();
            runParticularCommand(line);
        }
    }

    /**
     * Выполняет команды из указанного файла.
     * <p>
     * Читает файл построчно, интерпретируя каждую строку как команду.
     * Использует {@link java.io.InputStreamReader} для чтения в кодировке UTF-8.
     * </p>
     *
     * @param f файл со скриптом команд для выполнения
     */
    public static void runFile(File f) {
        Scanner oldScanner = sc;
        try (FileInputStream fis = new FileInputStream(f);
             InputStreamReader isr = new InputStreamReader(fis, "UTF-8")) {
            sc = new Scanner(isr);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                runParticularCommand(line);
            }
        } catch (Exception e) {

        } finally {
            sc = oldScanner;
        }
    }

    /**
     * Возвращает сканер для чтения пользовательского ввода.
     * <p>
     * Метод используется другими классами для получения доступа
     * к общему сканеру консоли.
     * </p>
     *
     * @return сканер консольного ввода
     */
    public static Scanner getScanner() {
        return sc;
    }

    /**
     * Выполняет конкретную команду с заданными аргументами.
     * <p>
     * Парсит строку команды, разделяя её на имя и аргументы.
     * Проверяет существование команды и корректность количества аргументов.
     * Для команд, реализующих {@link Modable}, устанавливает аргументы перед выполнением.
     * </p>
     *
     * @param line строка с именем команды и её аргументами
     */
    private static void runParticularCommand(String line){
        String[] tokens = line.split(" ");
        String commandName = tokens[0];
        ArrayList<String> args = new ArrayList<>();
        if (tokens.length > 1) {
            for (int i = 1; i < tokens.length; i++) {
                args.add(tokens[i]);
            }
        }
        Command command = commands.get(commandName);
        if (command == null) {
            System.out.println("Неизвестная команда: " + commandName);
            return;
        }
        if (command instanceof Modable) {
            if(args.size()==command.numberArgsRequired())
                ((Modable) command).setArguments(args);
            else {
                System.out.println("Слишком мало/много аргументов. Нужно " + command.numberArgsRequired() + " аргументов.");
                return;
            }
        }
        command.execute();
    }
}
