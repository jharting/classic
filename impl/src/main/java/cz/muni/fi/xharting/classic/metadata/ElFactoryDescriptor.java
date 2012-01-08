package cz.muni.fi.xharting.classic.metadata;

import org.jboss.seam.ScopeType;

/**
 * Represents a factory method configured in the component descriptor file.
 * 
 * @author Jozef Hartinger
 * 
 */
public class ElFactoryDescriptor extends AbstractFactoryDescriptor {

    private final String expression;
    private boolean valueExpression;

    public ElFactoryDescriptor(String name, ScopeType scope, boolean autoCreate, String expression, boolean valueExpression) {
        super(name, scope, autoCreate);
        if (!ScopeType.UNSPECIFIED.equals(scope)) {
            throw new IllegalStateException("Cannot use non-default scope for value expression factory");
        }
        this.expression = expression;
        this.valueExpression = valueExpression;
    }

    public String getExpression() {
        return expression;
    }

    public boolean isValueExpression() {
        return valueExpression;
    }

    @Override
    public String toString() {
        return "ElFactoryDescriptor mapping " + getName() + " to expression " + expression;
    }
}
