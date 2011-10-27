package org.jboss.seam.classic.test.intercept;

import static org.jboss.seam.classic.test.utils.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.classic.intercept.ClassicInterceptorBinding;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InterceptorTest {

    @Inject
    private InterceptedBean bean;

    @Inject
    private BeanManager manager;

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", true, false, BooleanInterceptor.class, IntegerInterceptor.class,
                InterceptedBean.class, InterceptorBindings.class).addAsWebInfResource(
                "org/jboss/seam/classic/test/intercept/beans.xml", "beans.xml");
    }

    @Test
    public void testPassivationCapable() {
        assertTrue(isPassivationCapable(BooleanInterceptor.class));
        assertFalse(isPassivationCapable(IntegerInterceptor.class));
    }

    private boolean isPassivationCapable(Class<?> clazz) {
        List<Interceptor<?>> interceptors = manager.resolveInterceptors(InterceptionType.AROUND_INVOKE,
                new ClassicInterceptorBinding.ClassicInterceptorBindingLiteral(clazz));
        assertEquals(1, interceptors.size());
        return interceptors.get(0) instanceof PassivationCapable;
    }

    @Test
    public void testInterception() {
        assertFalse(bean.getBool1());
        assertTrue(bean.getBool2());
        assertEquals(0, bean.getInt1());
        assertEquals(10, bean.getInt2());
    }

}
