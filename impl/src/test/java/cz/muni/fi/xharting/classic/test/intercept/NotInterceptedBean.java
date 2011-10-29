package cz.muni.fi.xharting.classic.test.intercept;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import cz.muni.fi.xharting.classic.test.intercept.InterceptorBindings.BooleanInterceptorBinding;
import cz.muni.fi.xharting.classic.test.intercept.InterceptorBindings.IntegerInterceptorBinding;

@Name("notInterceptedBean")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@BooleanInterceptorBinding
@IntegerInterceptorBinding
@Conversational
public class NotInterceptedBean {

    @In(create = true, value = "interceptedBean")
    private InterceptedBean bean;

    public int ping() {
        if (bean != null) {
            throw new IllegalStateException("Injection performed");
        }
        return 0;
    }

}
