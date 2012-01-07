package cz.muni.fi.xharting.classic.test.scope.conversation;

import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamWebApp;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.core.Conversation;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ConversationTest {

    @Inject
    private Wizzard wizzard;
    
    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war").addClass(Wizzard.class);
    }

    @Test
    public void testConversationApi() {
        Conversation conversation = Conversation.instance();
        assertFalse(conversation.isLongRunning());
        assertFalse(conversation.isNested());
        try {
            conversation.end();
            fail();
        } catch (IllegalStateException e) {
            // expected
        }
        conversation.begin();
        try {
            conversation.begin();
            fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }
    
    @Test
    public void testInterceptor() {
        assertFalse(wizzard.getConversation().isLongRunning());
        wizzard.nonMatchingBegin();
        assertFalse(wizzard.getConversation().isLongRunning());
        wizzard.begin();
        assertTrue(wizzard.getConversation().isLongRunning());
    }
}
