package cz.muni.fi.xharting.classic.test.config.beans;

import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.ScopeType;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import cz.muni.fi.xharting.classic.metadata.AbstractManagedInstanceDescriptor;
import cz.muni.fi.xharting.classic.metadata.ManagedBeanDescriptor;
import cz.muni.fi.xharting.classic.metadata.MetadataRegistry;
import cz.muni.fi.xharting.classic.test.config.beans.subpackage.Sparrow;

@RunWith(Arquillian.class)
public class XmlDefinedManagedBeanTest {

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", Eagle.class, Falcon.class, Pigeon.class, UltimatePigeon.class, Sparrow.class)
                .addPackage(Sparrow.class.getPackage())
                .addAsResource("cz/muni/fi/xharting/classic/test/config/beans/components.xml", "META-INF/components.xml")
                .addAsResource("cz/muni/fi/xharting/classic/test/config/beans/package-components.xml", "classes/cz/muni/fi/xharting/classic/test/config/beans/components.xml")
                .addAsResource("cz/muni/fi/xharting/classic/test/config/beans/Eagle3.components.xml", "META-INF/Eagle3.components.xml")
                .addAsResource("cz/muni/fi/xharting/classic/test/config/beans/components.properties", "components.properties");
    }

    @Inject
    private MetadataRegistry registry;

    @Test
    public void testBeanRegistration(@Named("eagle") Eagle eagle) {
        assertNotNull(eagle);
        ManagedBeanDescriptor descriptor = getManagedBeanDescriptorByName("eagle");
        assertEquals("eagle", descriptor.getImplicitRole().getName());
        assertEquals(ScopeType.SESSION, descriptor.getImplicitRole().getSpecifiedScope());
        assertEquals(true, descriptor.isAutoCreate());
    }
    
    @Test
    public void testBeanRegistrationPackageLevelFile() {
        ManagedBeanDescriptor descriptor = getManagedBeanDescriptorByName("eagle2");
        assertEquals("eagle2", descriptor.getImplicitRole().getName());
        assertEquals(ScopeType.APPLICATION, descriptor.getImplicitRole().getSpecifiedScope());
        assertEquals(true, descriptor.isAutoCreate());
    }
    
    @Test
    public void testBeanRegistrationSingleComponentFile() {
        ManagedBeanDescriptor descriptor = getManagedBeanDescriptorByName("eagle3");
        assertEquals("eagle3", descriptor.getImplicitRole().getName());
        assertEquals(ScopeType.EVENT, descriptor.getImplicitRole().getSpecifiedScope());
        assertEquals(false, descriptor.isAutoCreate());
    }

    @Test
    public void testBeanAlteration(@Named("falcon") Falcon falcon) {
        assertNotNull(falcon);
        ManagedBeanDescriptor descriptor = getManagedBeanDescriptorByName("falcon");
        assertEquals(ScopeType.APPLICATION, descriptor.getImplicitRole().getSpecifiedScope());
        assertEquals(false, descriptor.isAutoCreate());
        assertEquals(true, descriptor.getInstallDescriptor().isInstalled());
    }

    @Test
    public void testBeanOverriding(@Named("pigeon") Pigeon pigeon) {
        assertNotNull(pigeon);
        assertTrue(pigeon instanceof UltimatePigeon);
        ManagedBeanDescriptor descriptor = getManagedBeanDescriptorByName("pigeon");
        assertEquals(ScopeType.EVENT, descriptor.getImplicitRole().getSpecifiedScope());
        assertEquals(UltimatePigeon.class, descriptor.getJavaClass());
        assertEquals(30, descriptor.getInstallDescriptor().getPrecedence());
        assertEquals(false, descriptor.isAutoCreate());
        assertFalse(descriptor.getFactories().isEmpty());
        assertFalse(descriptor.getObserverMethods().isEmpty());
    }

    @Test
    public void testNamespace(@Named("sparrow") Sparrow sparrow) {
        assertNotNull(sparrow);
        assertEquals(13, sparrow.getAge());
        assertEquals("Ricki", sparrow.getName());

        ManagedBeanDescriptor descriptor = getManagedBeanDescriptorByName("sparrow");
        assertEquals(true, descriptor.isAutoCreate());
        assertEquals(ScopeType.APPLICATION, descriptor.getImplicitRole().getSpecifiedScope());
    }

    private ManagedBeanDescriptor getManagedBeanDescriptorByName(String name) {
        AbstractManagedInstanceDescriptor descriptor = registry.getManagedInstanceDescriptorByName(name);
        if (descriptor == null || !(descriptor instanceof ManagedBeanDescriptor)) {
            fail();
        }
        return (ManagedBeanDescriptor) descriptor;
    }

}
