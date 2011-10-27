package org.jboss.seam.classic.intercept;

import javax.interceptor.InvocationContext;

@SuppressWarnings("deprecation")
public class LegacyInvocationContext extends ForwardingInvocationContext implements org.jboss.seam.intercept.InvocationContext {

    private final InvocationContext delegate;

    public LegacyInvocationContext(InvocationContext delegate) {
        this.delegate = delegate;
    }

    @Override
    protected InvocationContext getDelegate() {
        return delegate;
    }

}
