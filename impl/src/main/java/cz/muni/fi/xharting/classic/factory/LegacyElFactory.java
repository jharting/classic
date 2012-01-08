package cz.muni.fi.xharting.classic.factory;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.el.Expressions;

import cz.muni.fi.xharting.classic.metadata.ElFactoryDescriptor;
import cz.muni.fi.xharting.classic.util.CdiUtils;

/**
 * Represents a factory method configured in the component descriptor file.
 * 
 * @author Jozef Hartinger
 * 
 */
public class LegacyElFactory extends AbstractLegacyFactory<Object> {

    private String expression;
    private boolean valueExpression;

    public LegacyElFactory(ElFactoryDescriptor descriptor, BeanManager manager) {
        // We must use dependent. Otherwise, we end up with unproxyable bean exception.
        super(descriptor.getName(), Dependent.class, manager);
        this.expression = descriptor.getExpression();
        this.valueExpression = descriptor.isValueExpression();
        addTypes(Object.class);
    }

    @Override
    public Object create(CreationalContext<Object> creationalContext) {
        Expressions expressions = CdiUtils.lookupBean(Expressions.class, getManager()).getInstance();
        if (valueExpression) {
            return expressions.evaluateValueExpression(expression);
        } else {
            return expressions.evaluateMethodExpression(expression);
        }
    }

    @Override
    public Class<?> getBeanClass() {
        return Object.class;
    }

    @Override
    public String toString() {
        return "LegacyElFactory mapping " + getName() + " to " + expression;
    }
}
