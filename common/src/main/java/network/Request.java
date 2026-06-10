package network;

import java.io.Serializable;

public class Request implements Serializable {
    private final CommandType commandType;
    private final CommandArgument commandArgument;
    private final String requestId;
    private final String login;
    private final String password;

    public Request(String requestId, CommandType commandType, CommandArgument commandArgument, String login, String password) {
        this.commandType = commandType;
        this.commandArgument = commandArgument;
        this.requestId = requestId;
        this.login = login;
        this.password = password;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getLogin() {return login;}

    public String getPassword() {return password;}

    public CommandType getCommandType() {
        return commandType;
    }

    public CommandArgument getCommandArgument() {
        return commandArgument;
    }
}
