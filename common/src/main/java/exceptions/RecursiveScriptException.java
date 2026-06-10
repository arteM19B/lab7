package exceptions;

public class RecursiveScriptException extends RuntimeException {

    private final String scriptPath;

    public RecursiveScriptException(String scriptPath) {
        super("Обнаружен рекурсивный вызов скрипта: " + scriptPath);
        this.scriptPath = scriptPath;
    }

    public String getScriptPath() {
        return scriptPath;
    }
}
