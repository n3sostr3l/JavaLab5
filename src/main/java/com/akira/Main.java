package com.akira;
import com.akira.command.Invoker;

import java.util.Hashtable;
import java.util.Scanner; // Импортируем инструмент для чтения ввода

public class Main {
    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Привет! Для вывода списка команд напиши help");
//
//        String command_str = scanner.nextLine();
//
//        String command = command_str.split(" ")[0];
//
////        Hashtable<String, Object> = new Hashtable<>();
//
//        while(!command.equals("exit")) {
//            if ("help".equals(command)) {
//                System.out.println("""
//                        help : вывести справку по доступным командам
//                        info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)
//                        show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении
//                        insert null {element} : добавить новый элемент с заданным ключом
//                        update id {element} : обновить значение элемента коллекции, id которого равен заданному
//                        remove_key null : удалить элемент из коллекции по его ключу
//                        clear : очистить коллекцию
//                        save : сохранить коллекцию в файл
//                        execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
//                        exit : завершить программу (без сохранения в файл)
//                        replace_if_greater null {element} : заменить значение по ключу, если новое значение больше старого
//                        replace_if_lowe null {element} : заменить значение по ключу, если новое значение меньше старого
//                        remove_lower_key null : удалить из коллекции все элементы, ключ которых меньше, чем заданный
//                        group_counting_by_maximum_point : сгруппировать элементы коллекции по значению поля maximumPoint, вывести количество элементов в каждой группе
//                        print_unique_author : вывести уникальные значения поля author всех элементов в коллекции
//                        print_field_descending_difficulty : вывести значения поля difficulty всех элементов в порядке убывания
//                        """);
//            }
//
//            if ("info".equals(command)) {
//
//            }
//            if ("show".equals(command)) {
//
//            }
//            if ("insert".equals(command)) {
//
//            }
//            if ("update".equals(command)) {
//
//            }
//            if ("remove_key_null".equals(command)) {
//
//            }
//            if ("clear".equals(command)) {
//
//            }
//            if ("save".equals(command)) {
//
//            }
//            if ("execute_script".equals(command)) {
//
//            }
//            if ("exit".equals(command)) {
//
//            }
//            if ("replace_if_greater".equals(command)) {
//
//            }
//            if ("replace_if_lowe".equals(command)) {
//
//            }
//            if ("group_counting_by_maximum_point".equals(command)) {
//
//            }
//            if ("print_unique_author".equals(command)) {
//
//            }
//            if ("print_field_descending_difficulty".equals(command)) {
//
//            }
//
//            command_str = scanner.nextLine();
//
//            command = command_str.split(" ")[0];
//
//        }
//        scanner.close();

        Invoker invoker = new Invoker();
        invoker.run();
    }
}