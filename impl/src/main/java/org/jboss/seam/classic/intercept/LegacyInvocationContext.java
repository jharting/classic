package org.jboss.seam.classic.intercept;

import javax.interceptor.InvocationContext;

import org.jboss.seam.classic.util.spi.ForwardingInvocationContext;

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
