package org.jboss.seam.classic.test.event;

import org.jboss.seam.annotations.RaiseEvent;

public class EventRaisingBean {

    @RaiseEvent
    public void foo() {
        // noop
    }

    @RaiseEvent({ "foo", "bar" })
    public void fooBar() {
        // noop
    }

    @RaiseEvent("bar")
    public Object bar(boolean throwException) {
        if (throwException) {
            throw new IllegalStateException();
        }
        return null;
    }
}
