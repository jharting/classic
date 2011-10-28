package org.jboss.seam.classic.init.scan;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface Scanner {

    public void scan();
    
    public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation);
}
