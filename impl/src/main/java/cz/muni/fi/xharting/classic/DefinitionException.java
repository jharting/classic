package cz.muni.fi.xharting.classic;

/**
 * Thrown when a definition error is detected.
 * 
 * @author Jozef Hartinger
 * 
 */
public class DefinitionException extends RuntimeException {

    private static final long serialVersionUID = 71127757336695970L;

    public DefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DefinitionException(String message) {
        super(message);
    }

    public DefinitionException(Throwable cause) {
        super(cause);
    }

}
