package org.jboss.seam.classic.test.runtime.outjection;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;

@Name("fooFactory")
public class Foo {

    @Factory("foo")
    public Message createMessage()
    {
        return new Message("foo");
    }
    
}
