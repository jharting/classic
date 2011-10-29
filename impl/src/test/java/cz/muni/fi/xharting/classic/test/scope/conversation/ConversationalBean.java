package cz.muni.fi.xharting.classic.test.scope.conversation;

import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Synchronized;

@Name("conversationalBean")
@Conversational
@Synchronized
public class ConversationalBean {

    public void ping() {
    }

}
