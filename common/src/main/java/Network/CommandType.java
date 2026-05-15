package Network;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

public enum CommandType implements Serializable {
    HELP("help"),
    INFO("info"),
    SHOW("show"),
    ADD("add"),
    UPDATE("update"),
    REMOVE_BY_ID("remove_by_id"),
    REMOVE_AT("remove_at"),
    CLEAR("clear"),
    REMOVE_LAST("remove_last"),
    SORT("sort"),
    REMOVE_ALL_BY_DISTANCE("remove_all_by_distance"),
    COUNT_GREATER_THAN_DISTANCE("count_greater_than_distance"),
    FILTER_LESS_THAN_DISTANCE("filter_less_than_distance"),
    REGISTER("register");

    private final String userName;

    CommandType(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public static Optional<CommandType> fromUserName(String userName) {
        return Arrays.stream(values())
                .filter(commandType -> commandType.userName.equals(userName))
                .findFirst();
    }
}
