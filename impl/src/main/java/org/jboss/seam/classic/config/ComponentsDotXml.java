package org.jboss.seam.classic.config;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.jboss.seam.ScopeType;
import org.jboss.seam.classic.init.metadata.AbstractFactoryDescriptor;
import org.jboss.seam.classic.init.metadata.AbstractObserverMethodDescriptor;
import org.jboss.seam.classic.init.metadata.ElFactoryDescriptor;
import org.jboss.seam.classic.init.metadata.ElObserverMethodDescriptor;
import org.jboss.seam.classic.util.XML;

public class ComponentsDotXml {

    private Element root;

    public ComponentsDotXml(InputStream stream) {
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

    public Set<AbstractFactoryDescriptor> getFactories() {
        Set<AbstractFactoryDescriptor> factories = new HashSet<AbstractFactoryDescriptor>();
        for (Element factory : elements(root, "factory")) {
            factories.add(parseFactory(factory));
        }
        return factories;
    }

    public Set<AbstractObserverMethodDescriptor> getObserverMethods() {
        Set<AbstractObserverMethodDescriptor> observerMethods = new HashSet<AbstractObserverMethodDescriptor>();
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

    private AbstractFactoryDescriptor parseFactory(Element factory) {
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
}
