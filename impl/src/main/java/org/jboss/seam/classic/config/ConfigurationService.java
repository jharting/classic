package org.jboss.seam.classic.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jboss.seam.classic.config.Conversions.PropertyValue;
import org.jboss.seam.classic.init.metadata.ElFactoryDescriptor;
import org.jboss.seam.classic.init.metadata.ElObserverMethodDescriptor;
import org.jboss.seam.classic.init.metadata.NamespaceDescriptor;

public class ConfigurationService {

    private static final String[] CONFIGURATION_FILE_NAMES = { "META-INF/components.xml", "components.xml" };
    private static final String REPLACEMENT_FILE_NAME = "components.properties";

    private final Set<ComponentsDotXml> configurationFiles = new HashSet<ComponentsDotXml>();
    private final Properties replacements = new Properties();

    public void loadConfiguration(Map<String, NamespaceDescriptor> namespaces)
    {
        loadReplacements();
        loadConfigurationFiles(namespaces);
    }
    
    protected void loadConfigurationFiles(Map<String, NamespaceDescriptor> namespaces) {
        for (String configurationFileName : CONFIGURATION_FILE_NAMES) {
            InputStream stream = null;
            try {
                Enumeration<URL> urls = this.getClass().getClassLoader().getResources(configurationFileName);
                while (urls.hasMoreElements()) {
                    URL resource = urls.nextElement();
                    stream = resource.openStream();
                    configurationFiles.add(new ComponentsDotXml(stream, namespaces, replacements));
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading configuration files.", e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    protected void loadReplacements() {
        InputStream stream = null;
        try {
            Enumeration<URL> urls = this.getClass().getClassLoader().getResources(REPLACEMENT_FILE_NAME);
            while (urls.hasMoreElements()) {
                stream = urls.nextElement().openStream();
                replacements.load(stream);
            }
        } catch (IOException ioe) {
            throw new RuntimeException("error reading components.properties", ioe);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public Set<ElFactoryDescriptor> getFactories() {
        Set<ElFactoryDescriptor> factories = new HashSet<ElFactoryDescriptor>();
        for (ComponentsDotXml configurationFile : configurationFiles) {
            factories.addAll(configurationFile.getFactories());
        }
        return factories;
    }

    public Set<ElObserverMethodDescriptor> getObserverMethods() {
        Set<ElObserverMethodDescriptor> observerMethods = new HashSet<ElObserverMethodDescriptor>();
        for (ComponentsDotXml configurationFile : configurationFiles) {
            observerMethods.addAll(configurationFile.getObserverMethods());
        }
        return observerMethods;
    }

    public Map<String, Map<String, Conversions.PropertyValue>> getInitialValueMap() {
        Map<String, Map<String, Conversions.PropertyValue>> values = new HashMap<String, Map<String, PropertyValue>>();
        for (ComponentsDotXml configurationFile : configurationFiles) {
            values.putAll(configurationFile.getInitialValueMap());
        }
        return values;
    }

    public Set<ConfiguredManagedBean> getConfiguredManagedBeans() {
        Set<ConfiguredManagedBean> beans = new HashSet<ConfiguredManagedBean>();
        for (ComponentsDotXml configurationFile : configurationFiles) {
            beans.addAll(configurationFile.getConfiguredManagedBeans());
        }
        return beans;
    }

}
