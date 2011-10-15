package org.jboss.seam.classic.config;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.classic.config.Conversions.PropertyValue;
import org.jboss.seam.classic.init.metadata.ElFactoryDescriptor;
import org.jboss.seam.classic.init.metadata.ElObserverMethodDescriptor;
import org.jboss.seam.classic.init.metadata.NamespaceDescriptor;

public class ConfigurationService {

    private static final String[] CONFIGURATION_FILE_NAMES = { "META-INF/components.xml", "components.xml" };

    private final Set<ComponentsDotXml> configurationFiles = new HashSet<ComponentsDotXml>();

    public void loadConfigurationFiles(Map<String, NamespaceDescriptor> namespaces) {
        try {
            for (String configurationFileName : CONFIGURATION_FILE_NAMES) {
                Enumeration<URL> urls = this.getClass().getClassLoader().getResources(configurationFileName);
                while (urls.hasMoreElements()) {
                    URL resource = urls.nextElement();
                    configurationFiles.add(new ComponentsDotXml(resource.openStream(), namespaces));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading configuration files.", e);
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
