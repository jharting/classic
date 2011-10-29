package cz.muni.fi.xharting.classic.test.factory.unwrap;

import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class UnwrapTest {

    @Inject
    @Named
    private List<Object> objects;
    
    @Inject
    private Model model;

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", Model.class, Objects.class);
    }

    @Test
    public void testUnwrapMethod() {
        assertEquals(0, objects.size());
        model.addObject();
        assertEquals(1, objects.size());
    }

}
