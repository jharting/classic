package org.jboss.seam.classic.init.factory;

/**
 * Placeholder for a product of a void factory method. It is considered an application error if an instance of this type is ever
 * to be injected.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 */
public class Void {

    public static final Void INSTANCE = new Void();

    public Void() {
    }

    /**
     * By default, a CDI implementation only creates a proxy for a bean instance. The instance itself is initialized lazily just
     * before a method is invoked on it.
     */
    public void forceBeanCreation() {
        // noop
    }

    @Override
    public String toString() {
        return "void";
    }
}
