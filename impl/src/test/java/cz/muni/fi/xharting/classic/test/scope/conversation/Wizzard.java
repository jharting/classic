package cz.muni.fi.xharting.classic.test.scope.conversation;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Conversation;

@Name("wizzard")
public class Wizzard {

    @In(value = "org.jboss.seam.core.conversation", create = true)
    private Conversation conversation;

    public Conversation getConversation() {
        return conversation;
    }

    @Begin(ifOutcome = "foo")
    public String nonMatchingBegin() {
        return "bar";
    }
    
    @Begin
    public void begin() {
    }
}
