package cz.muni.fi.xharting.classic.util;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Various annotation utilities
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
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

}
