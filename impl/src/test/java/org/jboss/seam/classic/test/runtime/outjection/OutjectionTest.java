package org.jboss.seam.classic.test.runtime.outjection;

import static org.jboss.seam.classic.test.utils.Archives.createSeamClassic;
import static org.jboss.seam.classic.test.utils.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.inject.Inject;
import javax.inject.Named;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.RequiredException;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.solder.el.Expressions;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class OutjectionTest {

    @Inject
    private InjectingBean injectingBean;
    @Inject
    private OutjectingBean outjectingBean;
    @Inject
    private Expressions expressions;
    // @ArquillianResource
    private URL contextPath;

    public OutjectionTest() {
        try {
            contextPath = new URL("http://localhost:8080");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Deployment
    public static WebArchive getDeployment() {
        WebArchive war = createSeamWebApp("test.war").addPackage(OutjectionTest.class.getPackage()).addAsLibrary(
                createSeamClassic());
        war.addAsWebResource("org/jboss/seam/classic/test/runtime/outjection/home.xhtml", "home.xhtml");
        war.addAsWebInfResource("org/jboss/seam/classic/test/runtime/outjection/faces-config.xml", "faces-config.xml");
        war.setWebXML("org/jboss/seam/classic/test/runtime/outjection/web.xml");
        return war;
    }

    @Test
    public void testExplicitScopeOutjection() {
        outjectingBean.ping();
        assertEquals("alpha", injectingBean.getAlpha().getValue());
        assertEquals("alpha", expressions.evaluateValueExpression("#{alpha.value}").toString());
    }

    @Test
    public void testImplicitScopeOutjection() {
        outjectingBean.ping();
        assertEquals("bravo", injectingBean.getBravo().getValue());
    }

    @Test
    public void testExplicitNameOutjection() {
        outjectingBean.ping();
        assertEquals("charlie", injectingBean.getCharlie().getValue());
    }

    @Test
    public void testOutjectedValueSelectedOverNonAutoCreateFactory() {
        outjectingBean.ping();
        assertEquals("delta", injectingBean.getDelta().getValue());
    }

    @Test
    public void testOutjectedValueSelectedOverAutoCreateFactory() {
        outjectingBean.ping();
        assertEquals("echo", injectingBean.getEcho().getValue());
    }

    @Test
    public void testAutoCreateFactorySelectedOverOutjectedValue() {
        outjectingBean.ping();
        assertEquals("foxtrot", injectingBean.getFoxtrot().getValue());
    }

    @Test
    public void testNullOutjection() {
        outjectingBean.ping();
        assertEquals("golf", injectingBean.getGolf().getValue());
        outjectingBean.setGolf(null);
        assertEquals("factoryGolf", injectingBean.getGolf().getValue());
        outjectingBean.setGolf(new Message("foobar"));
        assertEquals("foobar", injectingBean.getGolf().getValue());
    }

    @Test
    public void testRequiredValidation(
            @Named("outjectingBeanBrokenRequiredFieldNull") OutjectingBeanBrokenRequiredFieldNull brokenOutjectingBean) {
        try {
            brokenOutjectingBean.ping();
            Assert.fail("Expected exception not thrown");
        } catch (RequiredException e) {
            // expected
        }
    }

    @Test
    public void testVoidFactory() {
        assertEquals("hotel", injectingBean.getHotel());
    }

    @Test
    public void testOutjectedValuesAccessibleFromJsf() throws Exception {
        String homepage = doGet("/test/home.jsf");
        assertTrue(verifyValue(homepage, "alpha", "alpha"));
        assertTrue(verifyValue(homepage, "bravo", "bravo"));
        assertTrue(verifyValue(homepage, "charlie", "charlie"));
        assertTrue(verifyValue(homepage, "delta", "delta"));
        assertTrue(verifyValue(homepage, "echo", "echo"));
        assertTrue(verifyValue(homepage, "foxtrot", "foxtrot"));
        assertTrue(verifyValue(homepage, "hotel", "hotel"));
    }

    private boolean verifyValue(String content, String key, String value) {
        return content.contains(key + ":" + value);
    }

    private String doGet(String path) throws IOException {
        BufferedReader reader = null;
        try {
            URL homepage = new URL(contextPath, path);
            reader = new BufferedReader(new InputStreamReader(homepage.openStream()));
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            return builder.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
