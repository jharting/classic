package org.jboss.seam.classic.runtime.outjection;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.NormalScope;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

import org.jboss.seam.solder.core.Veto;

@Veto
public class OutjectedReferenceHolder implements Serializable {

    private static final long serialVersionUID = -3467806759929526350L;
    
    private Map<String, Object> values = new ConcurrentHashMap<String, Object>();

    public Object get(String name) {
        return values.get(name);
    }

    public void put(String name, Object value) {
        if (value == null) {
            values.remove(name);
        } else {
            values.put(name, value);
        }
    }

    public boolean contains(String name) {
        return values.containsKey(name);
    }
    
    @Qualifier
    @Target({ TYPE })
    @Retention(RUNTIME)
    @Documented
    public @interface ScopeQualifier {

        Class<? extends Annotation> value();

        @SuppressWarnings("all")
        public class ScopeQualifierLiteral extends AnnotationLiteral<ScopeQualifier> implements ScopeQualifier {

            private static final long serialVersionUID = 813098538363934584L;
            private Class<? extends Annotation> value;

            private ScopeQualifierLiteral(Class<? extends Annotation> value) {
                if (!value.isAnnotationPresent(NormalScope.class)) {
                    throw new IllegalArgumentException("Only normal-scope annotations are supported.");
                }
                this.value = value;
            }

            @Override
            public Class<? extends Annotation> value() {
                return value;
            }

            private static Map<Class<? extends Annotation>, ScopeQualifierLiteral> values = new HashMap<Class<? extends Annotation>, ScopeQualifier.ScopeQualifierLiteral>();

            public static ScopeQualifierLiteral valueOf(Class<? extends Annotation> context) {
                if (!context.isAnnotationPresent(NormalScope.class)) {
                    throw new IllegalArgumentException("Only normal-scope annotations are supported.");
                }
                if (!values.containsKey(context)) {
                    values.put(context, new ScopeQualifierLiteral(context));
                }
                return values.get(context);
            }
        }
    }
}
