package cz.muni.fi.xharting.classic.util.reference;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;

/**
 * Indicates that a direct reference to the given bean instance should always be injected instead of a client proxy. Using
 * direct reference has several implications:
 * 
 * Firstly, it is not allowed to inject A into B using direct reference if the scope of A is narrower that the scope of B.
 * 
 * Secondly, if A is injected into B and B declares a passivating scope, A must be passivation capable (as defined in the CDI
 * spec).
 * 
 * @author Jozef Hartinger
 * 
 */
@Target({ TYPE, METHOD, FIELD })
@Retention(RUNTIME)
@Documented
public @interface DirectReference {

    @SuppressWarnings("all")
    public static class Literal extends AnnotationLiteral<DirectReference> implements DirectReference {

        private static final long serialVersionUID = -8306192214595654373L;

        private Literal() {
        }

        public static final Literal INSTANCE = new Literal();
    }
}
