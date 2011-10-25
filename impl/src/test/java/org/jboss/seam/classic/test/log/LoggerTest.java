package org.jboss.seam.classic.test.log;

import static org.jboss.seam.classic.test.utils.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.classic.log.LogImpl;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.solder.el.Expressions;
import org.jboss.solder.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class LoggerTest {

    @Inject
    private LoggingBean bean;

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", LoggingBean.class, User.class);
    }

    /**
     * Only verifies that logging does not throw exceptions. Also, the output can be verified manually in the server log.
     */
    @Test
    public void testLogging() {
        bean.logSomething();
    }
    
    @Test
    public void testInterpolation(Expressions expressions)
    {
        TestableLogImpl log = new TestableLogImpl(null, expressions);
        String template = "Creating new order for user: #{user.username} quantity: #0 and another param #1";
        String result = log.interpolate(template);
        assertEquals("Creating new order for user: jharting quantity: {0} and another param {1}", result);
    }
    
    // make the interpolate method visible
    @SuppressWarnings("serial")
    private static class TestableLogImpl extends LogImpl
    {

        public TestableLogImpl(Logger delegate, Expressions expressions) {
            super(delegate, expressions);
        }

        @Override
        public String interpolate(Object object) {
            return super.interpolate(object);
        }
    }
}
