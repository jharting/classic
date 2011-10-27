package org.jboss.seam.classic.test.intercept;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.classic.test.intercept.InterceptorBindings.BooleanInterceptorBinding;
import org.jboss.seam.classic.test.intercept.InterceptorBindings.IntegerInterceptorBinding;

@Name("bean")
@Scope(ScopeType.EVENT)
public class InterceptedBean {

    @BooleanInterceptorBinding
    // should invert
    public boolean getBool1() {
        return true;
    }

    @IntegerInterceptorBinding
    // should do nothing
    public boolean getBool2() {
        return true;
    }

    @BooleanInterceptorBinding
    // should do nothing
    public int getInt1() {
        return 0;
    }

    @IntegerInterceptorBinding
    // should modify
    public int getInt2() {
        return 0;
    }
}
