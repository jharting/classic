package cz.muni.fi.xharting.classic.test.scope.conversation;

import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamWebApp;

import javax.inject.Named;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.NoConversationException;
import org.jboss.seam.annotations.Conversational;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ConversationalTest {

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", Conversational.class, ConversationalBean.class);
    }

    @Test(expected = NoConversationException.class)
    public void testNoConversationExceptionRaised(@Named("conversationalBean") ConversationalBean bean) {
        bean.ping();
    }

}
