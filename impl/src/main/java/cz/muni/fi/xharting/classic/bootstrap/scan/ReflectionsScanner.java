package cz.muni.fi.xharting.classic.bootstrap.scan;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.vfs.JBoss6UrlType;
import org.reflections.vfs.Vfs;

/**
 * Scanner implementation that uses Reflections.
 * 
 * @author Jozef Hartinger
 * 
 */
public class ReflectionsScanner extends AbstractScanner {

    private final Reflections reflections;
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    public ReflectionsScanner(ClassLoader loader) {
        Collection<URL> urls = getSeamArchives(loader);
        Vfs.addDefaultURLTypes(new JBoss6UrlType());
        Configuration configuration = new ConfigurationBuilder().setUrls(urls).setScanners(new ResourcesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner())
                .setExecutorService(executorService);
        reflections = new Reflections(configuration);
        executorService.shutdown();
    }

    @Override
    public void scan() {
        // noop, done automatically in constructor
    }

    @Override
    public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) {
        return reflections.getTypesAnnotatedWith(annotation);
    }

    @SuppressWarnings("unchecked")
    // checked with isAnnotation
    public Set<Class<?>> getNonAnnotationTypesAnnotatedWithMetaAnnotation(Class<? extends Annotation> annotation) {
        Set<Class<?>> nonAnnotationTypes = new HashSet<Class<?>>();
        for (Class<?> type : getTypesAnnotatedWith(annotation)) {
            if (type.isAnnotation()) {
                nonAnnotationTypes.addAll(getNonAnnotationTypesAnnotatedWithMetaAnnotation((Class<? extends Annotation>) type));
            } else {
                nonAnnotationTypes.add(type);
            }
        }
        return nonAnnotationTypes;
    }

    public Set<String> getResources(Pattern pattern) {
        return reflections.getResources(pattern);
    }
}
