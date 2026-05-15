package main.Commands;

import Network.CommandArgument;
import main.model.User;

/**
 * Интерфейс, который реализуют все команды программы.
 * Реализует паттерн "main.Commands.Command" для унифицированного выполнения действий.
 *
 * @author artem_bahetkin
 * @version 1.0
 */
public interface Command {
    String execute(CommandArgument argument, User user);
}
