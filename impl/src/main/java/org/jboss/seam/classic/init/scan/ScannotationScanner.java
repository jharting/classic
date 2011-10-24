package org.jboss.seam.classic.init.scan;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.solder.logging.Logger;
import org.scannotation.AnnotationDB;

public class ScannotationScanner {

    public static final String[] RESOURCE_NAMES = { "seam.properties", "META-INF/seam.properties", "META-INF/components.xml" };

    private static final Logger log = Logger.getLogger(ScannotationScanner.class);

    private ClassLoader classLoader;
    private Map<String, Set<String>> annotationIndex = new HashMap<String, Set<String>>();

    public ScannotationScanner(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    private Collection<URL> identifySeamArchives() {
        Set<URL> archives = new HashSet<URL>();
        for (String resourceName : RESOURCE_NAMES) {
            Enumeration<URL> urls;
            try {
                urls = classLoader.getResources(resourceName);
            } catch (IOException e) {
                log.warnv(e, "Unable to load Seam resource {0}", resourceName);
                continue;
            }
            while (urls.hasMoreElements()) {
                URL resource = urls.nextElement();
                String resourcePath = resource.getFile();
                try {
                    resourcePath = URLDecoder.decode(resourcePath, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    log.warnv("Unable to decode URL {0}", resourcePath);
                }
                log.debugv("Found Seam resource at the following url {0} and path {1}", resource, resourcePath);

                if (resourcePath.indexOf('!') > 0) { // no idea what ! in the path means
                    resourcePath = resourcePath.substring(0, resourcePath.indexOf('!'));
                } else {
                    File dirOrArchive = new File(resourcePath);
                    if (resourceName != null && resourceName.lastIndexOf('/') > 0) {
                        // for META-INF/components.xml
                        dirOrArchive = dirOrArchive.getParentFile();
                    }
                    resourcePath = dirOrArchive.getParent();
                }
                URL archiveUrl;
                try {
                    archiveUrl = new URL(resource.getProtocol(), resource.getHost(), resource.getPort(), resourcePath);
                } catch (MalformedURLException e) {
                    throw new IllegalStateException("Unable to identify Seam archive " + resource, e);
                }
                log.debugv("Identified Seam archive at {0}", archiveUrl);
                archives.add(archiveUrl);
            }
        }
        return archives;
    }

    public void scan() {
        Collection<URL> archives = identifySeamArchives();

        AnnotationDB db = new AnnotationDB();
        db.setScanClassAnnotations(true);
        db.setScanFieldAnnotations(false);
        db.setScanMethodAnnotations(false);
        db.setScanParameterAnnotations(false);
        try {
            db.scanArchives(archives.toArray(new URL[0]));
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO
        }
        annotationIndex = db.getAnnotationIndex();
    }

    /**
     * 
     * @param name FQN of an annotation
     * @return classes annotated with the annotation
     */
    public Set<String> getClassNames(String name) {
        Set<String> namedClasses = annotationIndex.get(name);
        if (namedClasses == null) {
            namedClasses = Collections.emptySet();
        }
        return namedClasses;
    }

    public Set<Class<?>> getClasses(String name) {
        Set<String> classNames = getClassNames(name);
        Set<Class<?>> classes = new HashSet<Class<?>>();
        for (String className : classNames) {
            log.debugv("Loading {0}", className);
            try {
                classes.add(classLoader.loadClass(className));
            } catch (ClassNotFoundException e) {
                log.warnv("Unable to load class {0}", className);
                log.debugv(e, "Unable to load class {0}", className);
            } catch (LinkageError e) {
                log.warnv("Unable to load class {0}", className);
                log.debugv(e, "Unable to load class {0}", className);
            }
        }
        return classes;
    }
}
