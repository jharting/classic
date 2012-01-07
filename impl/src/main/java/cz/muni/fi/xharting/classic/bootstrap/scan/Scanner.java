package cz.muni.fi.xharting.classic.bootstrap.scan;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Scanner that discovers Seam 2 components.
 * 
 * @author Jozef Hartinger
 * 
 */
public interface Scanner {

    public void scan();

    public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation);

    public Set<String> getResources(Pattern pattern);
}
