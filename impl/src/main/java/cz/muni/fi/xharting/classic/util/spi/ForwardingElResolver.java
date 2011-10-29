package cz.muni.fi.xharting.classic.util.spi;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

public abstract class ForwardingElResolver extends ELResolver {

    protected abstract ELResolver getDelegate();

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        return getDelegate().getValue(context, base, property);
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        return getDelegate().getType(context, base, property);
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        getDelegate().setValue(context, base, property, value);
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return getDelegate().isReadOnly(context, base, property);
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return getDelegate().getFeatureDescriptors(context, base);
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return getDelegate().getCommonPropertyType(context, base);
    }

}
