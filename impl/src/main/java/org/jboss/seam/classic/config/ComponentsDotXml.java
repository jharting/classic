package org.jboss.seam.classic.config;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.jboss.logging.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.classic.init.metadata.ElFactoryDescriptor;
import org.jboss.seam.classic.init.metadata.ElObserverMethodDescriptor;
import org.jboss.seam.classic.init.metadata.NamespaceDescriptor;
import org.jboss.seam.classic.util.Strings;
import org.jboss.seam.classic.util.XML;

/**
 * Represents a components.xml file.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 */
public class ComponentsDotXml {

    public static final String COMPONENT_NAMESPACE = "http://jboss.com/products/seam/components";

    private final Map<String, NamespaceDescriptor> namespaces;
    private final Properties replacements;

    private Element root;
    private boolean componentsParsed = false;
    private Map<String, Map<String, Conversions.PropertyValue>> initialValueMap = new HashMap<String, Map<String, Conversions.PropertyValue>>();
    private Set<ConfiguredManagedBean> configuredManagedBeans = new HashSet<ConfiguredManagedBean>();

    private static final Logger log = Logger.getLogger(ComponentsDotXml.class);

    @SuppressWarnings("serial")
    private static final Set<String> RESERVED_ATTRIBUTES = Collections.unmodifiableSet(new HashSet<String>() {
        {
            add("name");
            add("installed");
            add("scope");
            add("startup");
            add("startupDepends");
            add("class");
            add("jndi-name");
            add("precedence");
            add("auto-create");
        }
    });

    public ComponentsDotXml(InputStream stream, Map<String, NamespaceDescriptor> namespaces, Properties replacements) {
        this.namespaces = namespaces;
        this.replacements = replacements;
        try {
            this.root = XML.getRootElementSafely(stream);
        } catch (Exception e) {
            throw new RuntimeException("error while components.xml", e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Element> elements(Element rootElement, String name) {
        return rootElement.elements(name);
    }

    public Set<ElFactoryDescriptor> getFactories() {
        Set<ElFactoryDescriptor> factories = new HashSet<ElFactoryDescriptor>();
        for (Element factory : elements(root, "factory")) {
            factories.add(parseFactory(factory));
        }
        return factories;
    }

    public Set<ElObserverMethodDescriptor> getObserverMethods() {
        Set<ElObserverMethodDescriptor> observerMethods = new HashSet<ElObserverMethodDescriptor>();
        for (Element event : elements(root, "event")) {
            String type = event.attributeValue("type");
            if (type == null) {
                throw new IllegalArgumentException("must specify type for <event/> declaration");
            }
            for (Element action : elements(event, "action")) {
                String execute = action.attributeValue("execute");
                if (execute == null) {
                    throw new IllegalArgumentException("must specify execute for <action/> declaration");
                }
                observerMethods.add(new ElObserverMethodDescriptor(type, execute));
            }
        }
        return observerMethods;
    }

    public Map<String, Map<String, Conversions.PropertyValue>> getInitialValueMap() {
        lazyInitCheck();
        return Collections.unmodifiableMap(initialValueMap);
    }

    public Set<ConfiguredManagedBean> getConfiguredManagedBeans() {
        lazyInitCheck();
        return Collections.unmodifiableSet(configuredManagedBeans);
    }

    private ElFactoryDescriptor parseFactory(Element factory) {
        String scopeName = factory.attributeValue("scope");
        String name = factory.attributeValue("name");
        if (name == null) {
            throw new IllegalArgumentException("must specify name in <factory/> declaration");
        }
        ScopeType scope = scopeName == null ? ScopeType.UNSPECIFIED : ScopeType.valueOf(scopeName.toUpperCase());
        boolean autoCreate = Boolean.parseBoolean(factory.attributeValue("auto-create"));
        String method = factory.attributeValue("method");
        String value = factory.attributeValue("value");

        if (method != null && value != null) {
            throw new IllegalArgumentException("must specify either method or value in <factory/> declaration for variable: "
                    + name);
        }

        if (value != null) {
            return new ElFactoryDescriptor(name, scope, autoCreate, value, true);
        } else {
            return new ElFactoryDescriptor(name, scope, autoCreate, method, false);
        }
    }

    @SuppressWarnings("unchecked")
    public void parseComponents() {
        if (componentsParsed) {
            throw new IllegalStateException("components.xml already parsed");
        }
        try {
            for (Element component : elements(root, "component")) {
                parseComponent(component, component.attributeValue("name"), component.attributeValue("class"));
            }

            for (Element element : (List<Element>) root.elements()) {
                String ns = element.getNamespace().getURI();
                if (COMPONENT_NAMESPACE.equals(ns.toString()) || Strings.isEmpty(ns)) {
                    continue;
                }
                NamespaceDescriptor descriptor = namespaces.get(ns.toString());
                if (descriptor == null) {
                    log.warnv("namespace declared in components.xml does not resolve to a package: {0}", ns);
                }

                String name = element.attributeValue("name");
                String elemName = toCamelCase(element.getName(), true);
                String className = element.attributeValue("class");

                if (className == null) {
                    for (String packageName : descriptor.getPackageNames()) {
                        try {
                            className = packageName + "." + elemName;
                            Class.forName(className);
                        } catch (ClassNotFoundException e) {
                            className = null;
                        }
                    }
                    if (className == null) {
                        throw new IllegalArgumentException("No class associated with element " + element.getName());
                    }
                }
                if (name == null) {
                    Class<?> clazz = null;
                    try {
                        clazz = Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException("Unable to load class " + className, e);
                    }
                    if (clazz.isAnnotationPresent(Name.class)) {
                        name = clazz.getAnnotation(Name.class).value();
                    }
                    if (name == null) {
                        // finally, if we could not get the name from the XML name attribute,
                        // or from an @Name annotation on the class, imply it
                        String prefix = descriptor.getComponentPrefix();
                        String componentName = toCamelCase(element.getName(), false);
                        name = Strings.isEmpty(prefix) ? componentName : prefix + '.' + componentName;
                    }
                }
                parseComponent(element, name, className);

            }
            componentsParsed = true;
        } catch (Throwable e) {
            throw new IllegalArgumentException("Exception parsing components.xml", e);
        }

    }

    @SuppressWarnings("unchecked")
    protected void parseComponent(Element component, String componentName, String className) throws ClassNotFoundException {
        Map<String, Conversions.PropertyValue> initialValuesForComponent = new HashMap<String, Conversions.PropertyValue>();

        String installed = replace(component.attributeValue("installed"));
        String scope = replace(component.attributeValue("scope"));
        String startup = replace(component.attributeValue("startup"));
        String startupDepends = replace(component.attributeValue("startupDepends"));
        Class<?> clazz = (className != null) ? Class.forName(replace(className)) : null;
        String jndiName = replace(component.attributeValue("jndi-name"));
        String precedence = replace(component.attributeValue("precedence"));
        String autoCreate = replace(component.attributeValue("auto-create"));
        String name = replace(componentName);

        if (name == null) {
            if (clazz == null) {
                throw new IllegalArgumentException("must specify either class or name in <component/> declaration");
            }
            if (!clazz.isAnnotationPresent(Name.class)) {
                throw new IllegalArgumentException(
                        "Component class must have @Name annotation or name must be specified in components.xml: " + className);
            }
            name = clazz.getAnnotation(Name.class).value();
        }

        ConfiguredManagedBean bean = new ConfiguredManagedBean(name, installed, scope, startup, startupDepends, clazz,
                jndiName, precedence, autoCreate);
        configuredManagedBeans.add(bean);

        // process initial values
        for (Element prop : (List<Element>) component.elements()) {
            String propName = prop.attributeValue("name");
            if (propName == null) {
                propName = prop.getQName().getName();
            }
            String qualifiedPropName = toCamelCase(propName, false);
            initialValuesForComponent.put(qualifiedPropName, getPropertyValue(prop, qualifiedPropName, componentName));
        }

        for (Attribute prop : (List<Attribute>) component.attributes()) {
            String attributeName = prop.getName();
            if (isProperty(prop.getNamespaceURI(), attributeName)) {
                String qualifiedPropName = toCamelCase(prop.getQName().getName(), false);
                Conversions.PropertyValue propValue = null;
                try {
                    propValue = getPropertyValue(prop);
                    initialValuesForComponent.put(qualifiedPropName, propValue);
                } catch (Exception ex) {
                    throw new IllegalArgumentException(String.format(
                            "Exception setting property %s on component %s.  Expression %s evaluated to %s.",
                            qualifiedPropName, componentName, prop.getValue(), propValue), ex);

                }
            }
        }
        initialValueMap.put(componentName, initialValuesForComponent);
    }

    // Seam 2 code

    @SuppressWarnings("unchecked")
    private Conversions.PropertyValue getPropertyValue(Element prop, String componentName, String propName) {
        String typeName = prop.attributeValue("type");
        Class<?> type = null;
        try {
            if (typeName != null) {
                type = Class.forName(typeName);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find class " + typeName + " when setting up property " + propName);
        }

        List<Element> keyElements = prop.elements("key");
        List<Element> valueElements = prop.elements("value");

        if (valueElements.isEmpty() && keyElements.isEmpty()) {
            return new Conversions.FlatPropertyValue(trimmedText(prop, propName));
        } else if (keyElements.isEmpty()) {
            // a list-like structure
            int len = valueElements.size();
            String[] values = new String[len];
            for (int i = 0; i < len; i++) {
                values[i] = trimmedText(valueElements.get(i), propName);
            }
            return new Conversions.MultiPropertyValue(values, type);
        } else {
            // a map-like structure
            if (valueElements.size() != keyElements.size()) {
                throw new IllegalArgumentException("value elements must match key elements: " + propName);
            }
            Map<String, String> keyedValues = new LinkedHashMap<String, String>();
            for (int i = 0; i < keyElements.size(); i++) {
                String key = trimmedText(keyElements.get(i), propName);
                String value = trimmedText(valueElements.get(i), propName);
                keyedValues.put(key, value);
            }
            return new Conversions.AssociativePropertyValue(keyedValues, type);
        }
    }

    private Conversions.PropertyValue getPropertyValue(Attribute prop) {
        return new Conversions.FlatPropertyValue(trimmedText(prop));
    }

    // private String trimmedText(Attribute attribute, Properties replacements)
    private String trimmedText(Attribute attribute) {
        // return replace( attribute.getText(), replacements );
        return attribute.getText();
    }

    private static String toCamelCase(String hyphenated, boolean initialUpper) {
        StringTokenizer tokens = new StringTokenizer(hyphenated, "-");
        StringBuilder result = new StringBuilder(hyphenated.length());
        String firstToken = tokens.nextToken();
        if (initialUpper) {
            result.append(Character.toUpperCase(firstToken.charAt(0))).append(firstToken.substring(1));
        } else {
            result.append(firstToken);
        }
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            result.append(Character.toUpperCase(token.charAt(0))).append(token.substring(1));
        }
        return result.toString();
    }

    /**
     * component properties are non-namespaced and not in the reserved attribute list
     */
    private boolean isProperty(String namespaceURI, String attributeName) {
        return (namespaceURI == null || namespaceURI.length() == 0) && !RESERVED_ATTRIBUTES.contains(attributeName);
    }

    private void lazyInitCheck() {
        if (!componentsParsed) {
            parseComponents();
        }
    }

    private String trimmedText(Element element, String propName) {
        String text = element.getTextTrim();
        if (text == null) {
            throw new IllegalArgumentException("property value must be specified in element body: " + propName);
        }
        return replace(text);
    }

    private String replace(String value) {
        if (value != null && value.startsWith("@")) {
            value = replacements.getProperty(value.substring(1, value.length() - 1));
        }
        return value;
    }
}
