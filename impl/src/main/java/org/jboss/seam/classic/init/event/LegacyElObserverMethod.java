package org.jboss.seam.classic.init.event;

import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.classic.init.metadata.ElObserverMethodDescriptor;
import org.jboss.seam.classic.util.CdiUtils;
import org.jboss.solder.el.Expressions;

public class LegacyElObserverMethod extends AbstractLegacyObserverMethod {

    private Expressions expressions;
    private String methodExpression;

    public LegacyElObserverMethod(ElObserverMethodDescriptor descriptor, TransactionPhase phase, BeanManager manager) {
        super(descriptor.getType(), phase, true, manager);
        this.methodExpression = descriptor.getMethodExpression();
    }

    @Override
    public Class<?> getBeanClass() {
        return Object.class; // TODO is this the right thing to do?
    }

    @Override
    public void notify(EventPayload event) {
        if (expressions == null) {
            expressions = CdiUtils.lookupBean(Expressions.class, getManager()).getInstance();
        }
        expressions.evaluateMethodExpression(methodExpression);
    }

    @Override
    public String toString() {
        return "LegacyElObserverMethod [methodExpression=" + methodExpression + ", getQualifier()=" + getQualifier()
                + ", getTransactionPhase()=" + getTransactionPhase() + "]";
    }
}
