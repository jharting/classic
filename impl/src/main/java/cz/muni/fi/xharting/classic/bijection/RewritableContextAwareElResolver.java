package cz.muni.fi.xharting.classic.bijection;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

/**
 * An implementation of {@link ELResolver} which considers {@link OutjectedReferenceHolder} instances in the resolution process.
 * 
 * @author Jozef Hartinger
 * 
 */
public class RewritableContextAwareElResolver extends ELResolver {

    private ELResolver delegate;
    @Inject
    private RewritableContextManager rewritableContextManager;

    @Inject
    public RewritableContextAwareElResolver(BeanManager manager) {
        this.delegate = manager.getELResolver();
    }

    @Override
    public Object getValue(ELContext context, Object base, Object p) {
        if (base == null && p instanceof String) {
            String property = (String) p;
            Object result = getValue(property);

            // let's delegate to BM's ELResolver
            if (result == null) {
                result = delegate.getValue(context, base, p);
            }

            if (result != null) {
                context.setPropertyResolved(true);
            }
            return result;
        }
        return null;
    }

    private Object getValue(String property) {
        return rewritableContextManager.get(property);
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return false;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return null;
    }

}
