package cz.muni.fi.xharting.classic.test.scope.page;

import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import cz.muni.fi.xharting.classic.metadata.MetadataRegistry;
import cz.muni.fi.xharting.classic.scope.page.PageScoped;

@RunWith(Arquillian.class)
public class PageScopeTest {

    @Inject
    private MetadataRegistry registry;

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", Foo.class);
    }

    @Test
    public void testScope() {
        assertEquals(PageScoped.class, registry.getManagedBeanDescriptorByName("foo").getImplicitRole().getCdiScope());
    }

    // TODO expand

}
