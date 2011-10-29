package cz.muni.fi.xharting.classic;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

@Qualifier
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RUNTIME)
public @interface Seam2ManagedBean {

    public String value();

    @SuppressWarnings("all")
    public class Seam2ManagedBeanLiteral extends AnnotationLiteral<Seam2ManagedBean> implements Seam2ManagedBean {
        private static final long serialVersionUID = 6572751944235127613L;
        private String value;

        public Seam2ManagedBeanLiteral(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }
}
