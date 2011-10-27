package org.jboss.seam.classic.intercept;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.Interceptor;

public abstract class AbstractInterceptor<T> extends AbstractBean<T> implements Interceptor<T> {

    private final Set<Annotation> interceptorBindings;

    public AbstractInterceptor(Class<T> beanClass, Annotation... interceptorBindings) {
        super(beanClass, Dependent.class);
        this.interceptorBindings = new HashSet<Annotation>(Arrays.asList(interceptorBindings));
    }

    @Override
    public Set<Annotation> getInterceptorBindings() {
        return interceptorBindings;
    }
}
