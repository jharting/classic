package org.jboss.seam.classic.init.factory;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.classic.init.metadata.ElFactoryDescriptor;
import org.jboss.seam.classic.util.CdiUtils;
import org.jboss.seam.solder.el.Expressions;

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
        if (valueExpression)
        {
            return expressions.evaluateValueExpression(expression);
        }
        else
        {
            return expressions.evaluateMethodExpression(expression);
        }
    }

    @Override
    public void destroy(Object instance, CreationalContext<Object> creationalContext) {
        creationalContext.release();
    }
}
