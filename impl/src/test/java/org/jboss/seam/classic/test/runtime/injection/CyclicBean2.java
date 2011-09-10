package org.jboss.seam.classic.test.runtime.injection;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("cyclicBean2")
@AutoCreate
public class CyclicBean2 {

    @In(create = true)
    private CyclicBean1 cyclicBean1;

    public CyclicBean1 getCyclicBean1() {
        return cyclicBean1;
    }

    public String echo() {
        return "cyclicBean2";
    }
}
