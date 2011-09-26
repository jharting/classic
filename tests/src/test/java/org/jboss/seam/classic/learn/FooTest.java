package org.jboss.seam.classic.learn;

import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class FooTest extends SeamTest {

    @Test
    public void foo() throws Exception {
        new ComponentTest() {

            @Override
            protected void testComponents() throws Exception {
                Foo foo = (Foo) getInstance("foo");
                foo.ping();
//                assertNull(bar);
//                assertNotNull(bar);
//                assertEquals(bar, "bar");
            }

        }.run();
    }

}
