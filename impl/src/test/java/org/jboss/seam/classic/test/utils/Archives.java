package org.jboss.seam.classic.test.utils;

import java.io.InputStream;

import javax.enterprise.inject.spi.Extension;

import org.jboss.osgi.testing.ManifestBuilder;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.Interceptors;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.async.Schedule;
import org.jboss.seam.classic.Seam2ManagedBean;
import org.jboss.seam.classic.config.ComponentsDotXml;
import org.jboss.seam.classic.init.CoreExtension;
import org.jboss.seam.classic.init.event.EventsImpl;
import org.jboss.seam.classic.intercept.ClassicInterceptor;
import org.jboss.seam.classic.intercept.InterceptorExtension;
import org.jboss.seam.classic.log.LogImpl;
import org.jboss.seam.classic.runtime.BijectionInterceptor;
import org.jboss.seam.classic.runtime.outjection.RewritableContextManager;
import org.jboss.seam.classic.scope.ScopeExtension;
import org.jboss.seam.classic.scope.StatelessScoped;
import org.jboss.seam.classic.startup.StartupListener;
import org.jboss.seam.classic.util.CdiScopeUtils;
import org.jboss.seam.classic.util.literals.PreDestroyLiteral;
import org.jboss.seam.core.Events;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.log.Log;
import org.jboss.seam.util.StaticLookup;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

@SuppressWarnings("deprecation")
public class Archives {

    public static JavaArchive createSeamClassic() {
        JavaArchive jar =  ShrinkWrap
                .create(JavaArchive.class, "seam-classic.jar")
                .addPackages(true, CoreExtension.class.getPackage())
                // org.jboss.seam.classic
                .addPackage(Seam2ManagedBean.class.getPackage())
                // org.jboss.seam.classic.async
                .addPackage(Schedule.class.getPackage())
                // org.jboss.seam.classic.config
                .addPackage(ComponentsDotXml.class.getPackage())
                // org.jboss.seam.classic.init
                .addPackage(BijectionInterceptor.class.getPackage())
                // org.jboss.seam.classic.initercept
                .addPackage(ClassicInterceptor.class.getPackage())
                // org.jboss.seam.classic.log
                .addPackage(LogImpl.class.getPackage())
                // org.jboss.seam.classic.runtime
                .addPackages(true, PreDestroyLiteral.class.getPackage())
                // org.jboss.seam.classic.event
                .addPackages(true, EventsImpl.class.getPackage())
                // org.jboss.seam.classic.runtime.outjection
                .addPackages(true, RewritableContextManager.class.getPackage())
                // org.jboss.seam.classic.scope
                .addPackages(true, StatelessScoped.class.getPackage())
                // org.jboss.seam.classic.startup
                .addPackages(true, StartupListener.class.getPackage())
                // org.jboss.seam.classic.util
                .addPackage(CdiScopeUtils.class.getPackage())
                // api
                .addPackage(ScopeType.class.getPackage())
                .addPackages(true, Name.class.getPackage(), Events.class.getPackage(), StaticLookup.class.getPackage())
                .addPackage(RequestParameter.class.getPackage())
                .addPackage(Log.class.getPackage())
                .addPackage(Interceptors.class.getPackage())
                .addPackage(Log.class.getPackage())
                .addPackage(InvocationContext.class.getPackage())
                .addPackage(Log.class.getPackage())

                .addAsServiceProvider(Extension.class, CoreExtension.class, InterceptorExtension.class,
                        ScopeExtension.class).addAsManifestResource("META-INF/beans.xml", "beans.xml");
        addDependencyToManifest(jar, "org.slf4j.impl");
        return jar;
    }

    public static JavaArchive createSeamJar(String name, Class<?>... classes) {
        return ShrinkWrap.create(JavaArchive.class, name).addClasses(classes)
                .addAsResource(EmptyAsset.INSTANCE, "seam.properties");
    }

    public static WebArchive createSeamWebApp(String name, Class<?>... classes) {
        return createSeamWebApp(name, true, true, classes);
    }

    public static WebArchive createSeamWebApp(String name, boolean bundleSeamClassic, boolean bundleBeansDotXml,
            Class<?>... classes) {
        WebArchive war = ShrinkWrap.create(WebArchive.class, name).addAsResource(EmptyAsset.INSTANCE, "seam.properties")
                .addClasses(classes);
        if (bundleBeansDotXml) {
            // beans.xml should not be required, but we bundle it since we want CDI to be enabled for tests
            war.addAsWebInfResource("META-INF/beans.xml", "beans.xml");
        }

        if (bundleSeamClassic) {
            war.addAsLibrary(createSeamClassic()).addAsLibraries(Dependencies.SEAM_SOLDER, Dependencies.DOM4J, Dependencies.GUAVA, Dependencies.REFLECTIONS);
        }
        return war;
    }
    
    public static <T extends ManifestContainer<?>> T addDependencyToManifest(T archive, final String dependencies)
    {
        archive.setManifest(new Asset() {

            @Override
            public InputStream openStream() {
                return ManifestBuilder.newInstance().addManifestHeader("Dependencies", dependencies)
                        .openStream();
            }
        });
        return archive;
    }
}
