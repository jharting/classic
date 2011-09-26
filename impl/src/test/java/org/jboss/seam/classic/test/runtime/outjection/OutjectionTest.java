package org.jboss.seam.classic.test.runtime.outjection;

import static org.jboss.seam.classic.test.utils.Archives.createSeamClassic;
import static org.jboss.seam.classic.test.utils.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.inject.Named;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.RequiredException;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class OutjectionTest {

    @Inject
    private InjectingBean injectingBean;
    @Inject
    private OutjectingBean outjectingBean;

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war").addPackage(OutjectionTest.class.getPackage()).addAsLibrary(createSeamClassic());
    }

    @Test
    public void testExplicitScopeOutjection() {
        outjectingBean.ping();
        assertEquals("alpha", injectingBean.getAlpha());
    }

    @Test
    public void testImplicitScopeOutjection() {
        outjectingBean.ping();
        assertEquals("bravo", injectingBean.getBravo());
    }
    
    @Test
    public void testExplicitNameOutjection() {
        outjectingBean.ping();
        assertEquals("charlie", injectingBean.getCharlie());
    }
    
    @Test
    public void testOutjectedValueSelectedOverNonAutoCreateFactory()
    {
        outjectingBean.ping();
        assertEquals("delta", injectingBean.getDelta());
    }
    
    @Test
    public void testOutjectedValueSelectedOverAutoCreateFactory()
    {
        outjectingBean.ping();
        assertEquals("echo", injectingBean.getEcho());
    }
    
    @Test
    public void testAutoCreateFactorySelectedOverOutjectedValue()
    {
        outjectingBean.ping();
        assertEquals("factoryFoxtrot", injectingBean.getFoxtrot().getValue());
    }
    
    @Test
    public void testNullOutjection()
    {
        outjectingBean.ping();
        assertEquals("golf", injectingBean.getGolf().getValue());
        outjectingBean.setGolf(null);
        assertEquals("factoryGolf", injectingBean.getGolf().getValue());
        outjectingBean.setGolf(new StringWrapper("foobar"));
        assertEquals("foobar", injectingBean.getGolf().getValue());
    }
    
    @Test
    public void testRequiredValidation(@Named("outjectingBeanBrokenRequiredFieldNull") OutjectingBeanBrokenRequiredFieldNull brokenOutjectingBean)
    {
        try
        {
            brokenOutjectingBean.ping();
            Assert.fail("Expected exception not thrown");
        }
        catch (RequiredException e)
        {
            // expected
        }
    }

}
