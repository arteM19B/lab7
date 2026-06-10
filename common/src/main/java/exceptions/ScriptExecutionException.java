package exceptions;

public class ScriptExecutionException extends RuntimeException {
    private final String scriptName;
    private final int lineNumber;
    private final String command;

    public ScriptExecutionException(String message, String scriptName, int lineNumber, String command) {
        super(String.format("Ошибка в скрипте '%s' (строка '%d'): %s. Команда %s.",  scriptName, lineNumber, message, command));
        this.scriptName = scriptName;
        this.lineNumber = lineNumber;
        this.command = command;
    }
    public ScriptExecutionException(String message, String scriptName) {
        this(message, scriptName, -1, "unknown");
    }

    public String getScriptName() {
        return scriptName;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
