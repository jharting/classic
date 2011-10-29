package cz.muni.fi.xharting.classic.test;

import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class FooTest extends SeamTest {

    @Test
    public void foo() throws Exception {
        new ComponentTest() {

            @Override
            protected void testComponents() throws Exception {
                Foo foo = (Foo) getInstance("foo");
                foo.ping();
            }

        }.run();
    }

}
