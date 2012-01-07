package cz.muni.fi.xharting.classic.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.BeanManager;

/**
 * Various annotation-related utilities
 * 
 * @author Jozef Hartinger
 * 
 */
public class Annotations {

    private Annotations() {
    }

    /**
     * Searches a given set of annotations for an instance of a given annotation.
     * 
     * @param annotations set of annotations to search
     * @param expectedAnnotationType annotation type to look for
     * @return the first found instance of a given type in the set of annotation or null if matching annotation is not found
     */
    public static <T extends Annotation> T getAnnotation(Set<? extends Annotation> annotations, Class<T> expectedAnnotationType) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(expectedAnnotationType)) {
                return expectedAnnotationType.cast(annotation);
            }
        }
        return null;
    }

    /**
     * Returns qualifiers applied to a given bean.
     */
    public static Set<Annotation> getQualifiers(AnnotatedElement e, BeanManager manager) {
        Set<Annotation> result = new HashSet<Annotation>();
        for (Annotation annotation : e.getAnnotations()) {
            if (manager.isQualifier(annotation.annotationType())) {
                result.add(annotation);
            }
        }
        return result;
    }

}
