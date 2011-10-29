package cz.muni.fi.xharting.classic.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.intercept.Interceptors;
import org.jboss.solder.reflection.annotated.AnnotatedTypeBuilder;

import cz.muni.fi.xharting.classic.intercept.ClassicInterceptorBinding;
import cz.muni.fi.xharting.classic.scope.page.PageScoped;
import cz.muni.fi.xharting.classic.scope.stateless.StatelessScoped;

public class Seam2Utils {

    private Seam2Utils() {
    }

    /**
     * Translates Seam 2 ScopeType to matching CDI scope.
     */
    public static Class<? extends Annotation> transformExplicitLegacyScopeToCdiScope(ScopeType scope) {
        switch (scope) {
            case STATELESS:
                return StatelessScoped.class;
            case METHOD:
                throw new UnsupportedOperationException("Scope not supported"); // TODO
            case EVENT:
                return RequestScoped.class;
            case PAGE:
                return PageScoped.class;
            case CONVERSATION:
                return ConversationScoped.class;
            case SESSION:
                return SessionScoped.class;
            case APPLICATION:
                return ApplicationScoped.class;
            case BUSINESS_PROCESS:
                throw new UnsupportedOperationException("Scope not supported"); // TODO
            default:
                throw new IllegalArgumentException("Not an explicit scope " + scope);
        }
    }

    /**
     * Removes legacy Seam 2 interceptor bindings and registers matching CDI interceptor bindings.
     */
    public static void transformLegacyInterceptorBindings(AnnotatedTypeBuilder<?> builder) {
        // class
        for (Annotation annotation : builder.getJavaClass().getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(Interceptors.class)) {
                Interceptors legacyMetaAnnotation = annotation.annotationType().getAnnotation(Interceptors.class);
                builder.removeFromClass(legacyMetaAnnotation.annotationType());
                for (Class<?> interceptorClass : legacyMetaAnnotation.value()) {
                    builder.addToClass(new ClassicInterceptorBinding.ClassicInterceptorBindingLiteral(interceptorClass));
                }
            }
        }
        // methods
        for (Class<?> clazz = builder.getJavaClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Method method : clazz.getDeclaredMethods()) {
                for (Annotation annotation : method.getAnnotations()) {
                    if (annotation.annotationType().isAnnotationPresent(Interceptors.class)) {
                        Interceptors legacyMetaAnnotation = annotation.annotationType().getAnnotation(Interceptors.class);
                        builder.removeFromMethod(method, legacyMetaAnnotation.annotationType());
                        for (Class<?> interceptorClass : legacyMetaAnnotation.value()) {
                            builder.addToMethod(method, new ClassicInterceptorBinding.ClassicInterceptorBindingLiteral(
                                    interceptorClass));
                        }
                    }
                }
            }
        }
    }
}
