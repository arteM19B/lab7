package main;

import java.util.Scanner;

import collection.Coordinates;
import collection.Location;
import collection.Route;
import exceptions.ExitException;
/**
 * Класс-помощник для интерактивного и скриптового создания объектов {@link Route}.
 * Отвечает за чтение данных от пользователя с валидацией вводимых значений.
 *
 * @author artem_bahetkin
 * @version 1.0
 */
public class RouteBuilder {
    private final Scanner scanner;
    private final boolean isConsole;

    public RouteBuilder(Scanner scanner, boolean isConsole) {
        this.scanner = scanner;
        this.isConsole = isConsole;
    }

    public Route<Long> build() throws ExitException {
        String name = readName();
        Coordinates coordinates = readCoordinates();
        Location from = readLocation("откуда (from)", true);
        Location to = readLocation("куда (to)", true);
        long distance = readDistance();

        return new Route<>(name, coordinates, from, to, distance);
    }

    private String readName() throws ExitException{
        while (true) {
            printConsole("Введите имя маршрута (нужно хоть что-то ввести): ");
            String input = nextLine().trim();
            if (input.equals("exit")) throw new ExitException();
            if (input.isEmpty()) {
                printError("Имя не может быть пустым");
                continue;
            }
            return input;
        }
    }

    private Coordinates readCoordinates() throws ExitException {
        while (true) {
            printConsole("Координата X (Long, > -248): ");
            String xStr = nextLine().trim();
            if (xStr.equals("exit")) throw new ExitException();
            long x = 0;
            try {
                x = Long.parseLong(xStr);
                if (x <= -248) {
                    printError("X должен быть больше -248");
                    continue;
                }
            } catch (NumberFormatException e) {
                printError("Неверный формат числа");
                return readCoordinates();
            }
            printConsole("Координата Y (Integer, > -448): ");
            String yStr = nextLine().trim();
            if (yStr.equals("exit")) throw new ExitException();
            int y = 0;
            try {
                y = Integer.parseInt(yStr);
                if (y <= -448) {
                    printError("Y должен быть больше -448");
                    continue;
                }
            } catch (NumberFormatException e) {
                printError("Неверный формат числа");
                return readCoordinates();
            }
            return new Coordinates(x, y);
        }
    }

    private long readDistance() throws ExitException{
        while (true) {
            printConsole("Расстояние (long, > 1): ");
            String input = nextLine().trim();
            if (input.equals("exit")) throw new ExitException();
            try {
                long dist = Long.parseLong(input);
                if (dist <= 1) {
                    printError("Расстояние должно быть больше 1");
                    continue;
                }
                return dist;
            } catch (NumberFormatException e) {
                printError("Неверный формат числа");
            }
        }
    }

    private Location readLocation(String promptText, boolean canBeNull) throws ExitException{
        printConsole("Локация " + promptText + (canBeNull ? " (Enter для null)" : "") + "\n");
        printConsole("  Введите X Y (Float double) или Enter для null: ");
        String input = nextLine().trim();
        if (input.equals("exit")) throw new ExitException();

        if (input.isEmpty() && canBeNull) {
            return null;
        }

        if (input.isEmpty()) {
            printError("Поле не может быть пустым");
            return readLocation(promptText, canBeNull);
        }

        String[] parts = input.split("\\s+");
        if (parts.length != 2) {
            printError("Ожидается два числа: X Y");
            return readLocation(promptText, canBeNull);
        }

        try {
            float x = Float.parseFloat(parts[0]);
            double y = Double.parseDouble(parts[1]);

            printConsole("  Имя локации (или Enter для null): ");
            String name = nextLine().trim();
            if (name.equals("exit")) throw new ExitException();
            if (name.isEmpty()) name = null;

            return new Location(x, y, name);
        } catch (NumberFormatException e) {
            printError("Неверный формат координат");
            return readLocation(promptText, canBeNull);
        }
    }

    private String nextLine() throws ExitException {
        try {
            if (scanner.hasNextLine()) {
                return scanner.nextLine();
            } else {
                throw new ExitException();
            }
        } catch (Exception e) {
            throw new ExitException();
        }
    }

    private void printConsole(String msg) {
        if (isConsole) {
            System.out.print(msg);
        }
    }

    private void printError(String msg) {
        if (isConsole) {
            System.out.println("Ошибка: " + msg + ". Повторите ввод.");
        } else {
            throw new IllegalArgumentException(msg);
        }
    }

}
