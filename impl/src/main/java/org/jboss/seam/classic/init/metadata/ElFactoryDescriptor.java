package org.jboss.seam.classic.init.metadata;

import org.jboss.seam.ScopeType;

public class ElFactoryDescriptor extends AbstractFactoryDescriptor {

    private final String expression;
    private boolean valueExpression;

    public ElFactoryDescriptor(String name, ScopeType scope, boolean autoCreate, String expression,
            boolean valueExpression) {
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
}
