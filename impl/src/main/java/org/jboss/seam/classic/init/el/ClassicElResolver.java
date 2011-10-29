package org.jboss.seam.classic.init.el;

import javax.el.ELResolver;

import org.jboss.seam.classic.util.literals.CompositeLiteral;
import org.jboss.seam.classic.util.spi.ForwardingElResolver;
import org.jboss.seam.util.StaticLookup;

public class ClassicElResolver extends ForwardingElResolver {

    private ELResolver delegate;
    
    @Override
    protected ELResolver getDelegate() {
        if (delegate == null)
        {
            delegate = StaticLookup.lookupBean(ELResolver.class, new CompositeLiteral());
        }
        return delegate;
    }

}
