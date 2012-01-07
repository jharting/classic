package cz.muni.fi.xharting.classic.test.util.reference;

import static org.junit.Assert.assertEquals;

import javax.enterprise.inject.spi.Extension;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import cz.muni.fi.xharting.classic.test.util.Dependencies;
import cz.muni.fi.xharting.classic.util.Annotations;
import cz.muni.fi.xharting.classic.util.reference.DirectReferenceExtension;
import cz.muni.fi.xharting.classic.util.spi.AbstractBean;

@RunWith(Arquillian.class)
public class DirectReferenceHolderTest {

    @Deployment
    public static WebArchive getDeployment() {
        return ShrinkWrap.create(WebArchive.class).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addPackage(Foo.class.getPackage()).addPackage(DirectReferenceExtension.class.getPackage())
                .addPackage(AbstractBean.class.getPackage())
                .addPackage(Annotations.class.getPackage())
                .addAsServiceProvider(Extension.class, DirectReferenceExtension.class)
                .addAsLibraries(Dependencies.SEAM_SOLDER, Dependencies.REFLECTIONS);
    }

    @Test
    public void testInjectedReferenceNotProxied(Foo foo, String bar) {
        assertEquals(String.class, bar.getClass());
        assertEquals("bar", bar);
        assertEquals(Foo.class, foo.getClass());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testScopeCheck(Alpha bean) {
        bean.toString();
    }
}
