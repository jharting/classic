package cz.muni.fi.xharting.classic.test.intercept;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.seam.annotations.intercept.Interceptors;

public class InterceptorBindings {

    private InterceptorBindings() {
    }

    @Inherited
    @Target({ TYPE, METHOD })
    @Retention(RUNTIME)
    @Interceptors(BooleanInterceptor.class)
    public @interface BooleanInterceptorBinding {
    }

    @Inherited
    @Target({ TYPE, METHOD })
    @Retention(RUNTIME)
    @Interceptors(IntegerInterceptor.class)
    public @interface IntegerInterceptorBinding {
    }
}
