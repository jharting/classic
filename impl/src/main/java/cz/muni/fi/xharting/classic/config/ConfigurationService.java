package cz.muni.fi.xharting.classic.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Multimap;

import cz.muni.fi.xharting.classic.bootstrap.scan.Scanner;
import cz.muni.fi.xharting.classic.config.Conversions.PropertyValue;
import cz.muni.fi.xharting.classic.metadata.ElFactoryDescriptor;
import cz.muni.fi.xharting.classic.metadata.ElObserverMethodDescriptor;
import cz.muni.fi.xharting.classic.metadata.BeanDescriptor;
import cz.muni.fi.xharting.classic.metadata.NamespaceDescriptor;

/**
 * Provides common operations over a set of component descriptor files.
 * 
 * @author Jozef Hartinger
 * 
 */
public class ConfigurationService {

    private static final Pattern CONFIGURATION_FILE_PATTERN = Pattern.compile(".*components.xml");
    private static final String[] REPLACEMENT_FILE_NAMES = { "components.properties" };

    private final Set<ComponentsDotXml> configurationFiles = new HashSet<ComponentsDotXml>();
    private final Properties replacements = new Properties();
    private Scanner scanner;

    public ConfigurationService(Scanner scanner) {
        this.scanner = scanner;
    }

    public void loadConfiguration(Map<String, NamespaceDescriptor> namespaces) {
        loadReplacements();
        loadConfigurationFiles(namespaces);
    }

    protected void loadConfigurationFiles(Map<String, NamespaceDescriptor> namespaces) {
        InputStream stream = null;
        for (String fileName : scanner.getResources(CONFIGURATION_FILE_PATTERN)) {
            try {
                Enumeration<URL> urls = this.getClass().getClassLoader().getResources(fileName);
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
        for (String fileName : REPLACEMENT_FILE_NAMES) {
            try {
                Enumeration<URL> urls = this.getClass().getClassLoader().getResources(fileName);
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

    /**
     * Apply configuration of managed beans ({@link #getConfiguredManagedBeans()}) to descriptors of managed beans passed as
     * parameter.
     * 
     * @param discoveredManagedBeanDescriptors managed bean configuration to merge with
     * @return managed bean descriptors with applied configuration changes
     */
    public Multimap<String, BeanDescriptor> mergeManagedBeanConfiguration(Multimap<String, BeanDescriptor> discoveredManagedBeanDescriptors) {
        for (ConfiguredManagedBean configuredBean : getConfiguredManagedBeans()) {
            Collection<BeanDescriptor> descriptors = discoveredManagedBeanDescriptors.get(configuredBean.getName());
            if (configuredBean.getClazz() == null) // the XML element specifies not scope
            {
                if (descriptors.size() == 1) {
                    Iterator<BeanDescriptor> iterator = descriptors.iterator();
                    BeanDescriptor reconfigured = new BeanDescriptor(configuredBean, iterator.next());
                    iterator.remove();
                    descriptors.add(reconfigured);
                } else {
                    throw new IllegalStateException("Cannot reconfigure bean: " + configuredBean.getName() + ". Exactly one candidate required but there are: "
                            + descriptors.toString());
                }
            } else {
                Set<BeanDescriptor> replacements = new HashSet<BeanDescriptor>();
                for (Iterator<BeanDescriptor> iterator = descriptors.iterator(); iterator.hasNext();) {
                    BeanDescriptor descriptor = iterator.next();
                    if (configuredBean.getClazz().equals(descriptor.getJavaClass())) {
                        // remove the original bean, install the replacement later
                        replacements.add(new BeanDescriptor(configuredBean, descriptor));
                        iterator.remove();
                    }
                }
                if (replacements.isEmpty()) {
                    // if the configured bean did not match any discovered bean, add it as a new bean
                    descriptors.add(new BeanDescriptor(configuredBean));
                } else {
                    // install reconfigured beans
                    descriptors.addAll(replacements);
                }

            }
        }
        return discoveredManagedBeanDescriptors;
    }

}
