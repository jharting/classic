package cz.muni.fi.xharting.classic.el;

import javax.el.ELResolver;

import org.jboss.seam.util.StaticLookup;

import cz.muni.fi.xharting.classic.util.literal.CompositeLiteral;
import cz.muni.fi.xharting.classic.util.spi.ForwardingElResolver;

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
