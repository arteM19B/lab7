package exceptions;

public class ExitException extends RuntimeException {
    public ExitException() {
        super("exit");
    }
}
