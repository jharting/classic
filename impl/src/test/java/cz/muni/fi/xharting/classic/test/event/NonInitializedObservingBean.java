package cz.muni.fi.xharting.classic.test.event;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

@Name("nonInitializedObservingBean")
@Scope(ScopeType.APPLICATION)
public class NonInitializedObservingBean {

    private static boolean observerCalled = false;
    
    @Observer(value = "ignoredEvent", create = false)
    public void observeEvent() {
        observerCalled = true;
    }

    public static boolean isObserverCalled() {
        return observerCalled;
    }
}
