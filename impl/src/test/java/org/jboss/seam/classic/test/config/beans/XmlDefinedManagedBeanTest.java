package org.jboss.seam.classic.test.config.beans;

import static org.jboss.seam.classic.test.utils.Archives.createSeamWebApp;
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
import org.jboss.seam.classic.init.metadata.AbstractManagedInstanceDescriptor;
import org.jboss.seam.classic.init.metadata.ManagedBeanDescriptor;
import org.jboss.seam.classic.init.metadata.MetadataRegistry;
import org.jboss.seam.classic.test.config.beans.subpackage.Sparrow;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class XmlDefinedManagedBeanTest {

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", Eagle.class, Falcon.class, Pigeon.class, UltimatePigeon.class, Sparrow.class)
                .addPackage(Sparrow.class.getPackage())
                .addAsResource("org/jboss/seam/classic/test/config/beans/components.xml", "META-INF/components.xml")
                .addAsResource("org/jboss/seam/classic/test/config/beans/components.properties", "components.properties");
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
