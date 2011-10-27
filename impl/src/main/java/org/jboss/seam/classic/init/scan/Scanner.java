package org.jboss.seam.classic.init.scan;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface Scanner {

    public abstract Set<Class<?>> getClasses(Class<? extends Annotation> annotation);

    public abstract Set<String> getClassNames(String name);

}
