package be.chipit.jet.common;

public class JetException extends RuntimeException {

    public JetException(String message) {
        super(message);
    }

    public JetException(String message, Throwable cause) {
        super(message, cause);
    }
}
