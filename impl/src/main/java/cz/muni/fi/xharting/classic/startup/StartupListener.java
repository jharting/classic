package cz.muni.fi.xharting.classic.startup;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


import cz.muni.fi.xharting.classic.metadata.ManagedBeanDescriptor;
import cz.muni.fi.xharting.classic.metadata.MetadataRegistry;
import cz.muni.fi.xharting.classic.metadata.RoleDescriptor;
import cz.muni.fi.xharting.classic.util.CdiUtils;

@WebListener
public class StartupListener implements ServletContextListener, HttpSessionListener {

    @Inject
    private MetadataRegistry registry;
    @Inject
    private BeanManager manager;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        startup(SessionScoped.class);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        startup(ApplicationScoped.class);
    }

    protected void startup(Class<? extends Annotation> scope) {
        Set<String> started = new HashSet<String>(); // so that we do not start the component multiple times
        for (RoleDescriptor role : registry.getStartupBeans(scope)) {
            startup(role.getName(), started);
        }
    }

    protected void startup(String name, Set<String> started) {
        if (started.contains(name)) {
            return; // started already
        }
        ManagedBeanDescriptor descriptor = registry.getManagedBeanDescriptorByName(name);
        if (descriptor != null) {
            for (String dependency : descriptor.getStartupDependencies()) {
                startup(dependency, started);
            }
            if (descriptor.isStartup()) {
                Object reference = CdiUtils.lookupBeanByInternalName(name, Object.class, manager).getInstance();
                reference.toString(); // toString() hack to actually instantiate the instance behind proxy
                started.add(name);
            }
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // noop
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // noop
    }
}
