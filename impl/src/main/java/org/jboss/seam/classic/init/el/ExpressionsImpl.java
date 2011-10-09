package org.jboss.seam.classic.init.el;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.inject.Inject;

import org.jboss.seam.core.Expressions;

// TODO scope;
public class ExpressionsImpl extends Expressions {

    private static final long serialVersionUID = -2788774416068475103L;
    private org.jboss.seam.solder.el.Expressions expressions;
    private ELContext context;

    @Inject
    public void init(org.jboss.seam.solder.el.Expressions expressions) {
        this.expressions = expressions;
        this.context = expressions.getELContext();
    }

    @Override
    public ExpressionFactory getExpressionFactory() {
        return expressions.getExpressionFactory();
    }

    @Override
    public ELContext getELContext() {
        return expressions.getELContext();
    }

    @Override
    public ValueExpression<Object> createValueExpression(String expression) {
        return createValueExpression(expression, Object.class);
    }

    @Override
    public MethodExpression<Object> createMethodExpression(String expression) {
        return createMethodExpression(expression, Object.class);
    }

    @Override
    public <T> ValueExpression<T> createValueExpression(String expression, Class<T> type) {
        javax.el.ValueExpression delegate = expressions.getExpressionFactory().createValueExpression(context, expression, type);
        return new ForwardingValueExpression<T>(delegate, context);
    }

    @Override
    public <T> MethodExpression<T> createMethodExpression(String expression, Class<T> type, Class... argTypes) {
        javax.el.MethodExpression delegate = expressions.getExpressionFactory().createMethodExpression(context, expression, type, argTypes);
        return new ForwardingMethodExpression<T>(delegate, context);
    }

    @SuppressWarnings("unchecked")
    private static class ForwardingValueExpression<T> implements Expressions.ValueExpression<T> {
        private static final long serialVersionUID = -5399093223686978929L;
        private javax.el.ValueExpression delegate;
        private ELContext context;

        public ForwardingValueExpression(javax.el.ValueExpression delegate, ELContext context) {
            this.delegate = delegate;
            this.context = context;
        }

        @Override
        public T getValue() {
            return (T) delegate.getValue(context);
        }

        @Override
        public String getExpressionString() {
            return delegate.getExpressionString();
        }

        @Override
        public Class<T> getType() {
            return (Class<T>) delegate.getExpectedType();
        }

        @Override
        public javax.el.ValueExpression toUnifiedValueExpression() {
            return delegate;
        }

        @Override
        public void setValue(T value) {
            delegate.setValue(context, value);
        }
    }

    @SuppressWarnings("unchecked")
    private static class ForwardingMethodExpression<T> implements Expressions.MethodExpression<T> {

        private static final long serialVersionUID = -2474548142003084216L;
        private javax.el.MethodExpression delegate;
        private ELContext context;
        
        public ForwardingMethodExpression(javax.el.MethodExpression delegate, ELContext context) {
            this.delegate = delegate;
            this.context = context;
        }

        @Override
        public T invoke(Object... args) {
            return (T) delegate.invoke(context, args);
        }

        @Override
        public String getExpressionString() {
            return delegate.getExpressionString();
        }

        @Override
        public javax.el.MethodExpression toUnifiedMethodExpression() {
            return delegate;
        }
    }
}
