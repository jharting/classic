package cz.muni.fi.xharting.classic.metadata;

/**
 * Represents an observer method configured in the component descriptor file.
 * 
 * @author Jozef Hartinger
 * 
 */
public class ElObserverMethodDescriptor extends AbstractObserverMethodDescriptor {

    private final String methodExpression;

    public ElObserverMethodDescriptor(String type, String methodExpression) {
        super(type);
        this.methodExpression = methodExpression;
    }

    public String getMethodExpression() {
        return methodExpression;
    }
}
