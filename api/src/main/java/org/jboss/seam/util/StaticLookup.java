package org.jboss.seam.util;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class StaticLookup {

    private static final String DEFAULT_LOCATION = "java:comp/BeanManager";
    
    public static BeanManager lookupBeanManager() {
        try {
            return (BeanManager) new InitialContext().lookup(DEFAULT_LOCATION);
        } catch (NamingException e) {
            throw new IllegalStateException("Unable to lookup BeanManager");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T lookupBean(Class<T> clazz, Annotation... qualifiers) {
        BeanManager manager = lookupBeanManager();
        Set<Bean<?>> beans = manager.getBeans(clazz, qualifiers);
        Bean<?> bean = manager.resolve(beans);
        if (bean == null) {
            throw new UnsatisfiedResolutionException("Unable to lookup " + clazz.getName());
        }
        CreationalContext<?> ctx = manager.createCreationalContext(bean);
        return (T) manager.getReference(bean, clazz, ctx);
    }
}
