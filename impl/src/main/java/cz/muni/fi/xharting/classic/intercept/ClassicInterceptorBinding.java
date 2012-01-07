package cz.muni.fi.xharting.classic.intercept;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;

/**
 * Internal interceptor binding. Similarly Seam 2 interceptor bindings, this binding maps directly to an interceptor class.
 * 
 * @author Jozef Hartinger
 * 
 */
@Inherited
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Documented
public @interface ClassicInterceptorBinding {

    Class<?> value();

    @SuppressWarnings("all")
    public static class ClassicInterceptorBindingLiteral extends AnnotationLiteral<ClassicInterceptorBinding> implements
            ClassicInterceptorBinding {

        private static final long serialVersionUID = 5454163206338191633L;
        private final Class<?> value;

        public ClassicInterceptorBindingLiteral(Class<?> value) {
            this.value = value;
        }

        @Override
        public Class<?> value() {
            return value;
        }

        @Override
        public String toString() {
            return "Legacy binding for " + value;
        }

    }
}
