package cz.muni.fi.xharting.classic.scope.stateless;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;

/**
 * Stateless scoped beans are created for every method invocation. Their state is not kept by the container.
 * 
 * @author Jozef Hartinger
 * 
 */
@Inherited
@Target({ TYPE, METHOD, FIELD })
@Retention(RUNTIME)
@Documented
public @interface StatelessScoped {

    @SuppressWarnings("all")
    public static class StatelessScopedLiteral extends AnnotationLiteral<StatelessScoped> implements StatelessScoped {
        public static final StatelessScoped INSTANCE = new StatelessScopedLiteral();
    }
}
