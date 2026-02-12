import java.util.Scanner; // Импортируем инструмент для чтения ввода

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Привет! Как тебя зовут?");
        
        String name = scanner.nextLine();

        System.out.println("Приятно познакомиться, " + name + "!");
        System.out.println("Добро пожаловать в мир Java!");

        scanner.close();
    }
}