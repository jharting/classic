package org.jboss.seam.classic.test.startup;

import static org.jboss.seam.classic.test.utils.Archives.createSeamWebApp;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class CyclicStartupTest {

    @Deployment
    @ShouldThrowException(Exception.class)
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", true, Cyclic1.class, Cyclic2.class);
    }

    @Test
    public void test() {
        // the deployment should throw exception
    }

}
