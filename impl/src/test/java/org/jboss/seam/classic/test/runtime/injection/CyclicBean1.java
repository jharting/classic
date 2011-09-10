package org.jboss.seam.classic.test.runtime.injection;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("cyclicBean1")
@AutoCreate
public class CyclicBean1 {

    @In
    private CyclicBean2 cyclicBean2;

    public CyclicBean2 getCyclicBean2() {
        return cyclicBean2;
    }

    public String echo() {
        return "cyclicBean1";
    }
}
