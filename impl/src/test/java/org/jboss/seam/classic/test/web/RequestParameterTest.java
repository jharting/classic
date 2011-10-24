package org.jboss.seam.classic.test.web;

import static org.jboss.seam.classic.test.utils.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class RequestParameterTest {

    @ArquillianResource
    private URL url;

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", InjectedBean.class, SimpleServlet.class);
    }

    @Test
    public void testRequestParameter() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url, "/test/classic/test?name=mississippi&id=12345").openConnection();
        assertEquals(200, connection.getResponseCode());
    }

}
