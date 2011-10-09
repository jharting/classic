package org.jboss.seam.classic.test.scope;

import static org.jboss.seam.classic.test.utils.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TestStatelessScope {

    @Inject
    private Book book;

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", true, Book.class);
    }

    @Test
    public void testEveryMethodInvocationExecutedOnDifferentInstance() {
        int id = book.getId();
        for (int i = 1; i < 100; i++) {
            assertEquals(++id, book.getId()); // every invocation should give us a different id
        }
        // the last instance is never destroyed properly
        // there's nothing we can do about that
        assertEquals(book.getId() - 1, Book.getLastDestroyed());
    }
}
