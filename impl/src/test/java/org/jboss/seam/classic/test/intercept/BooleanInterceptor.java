package org.jboss.seam.classic.test.intercept;

import java.io.Serializable;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.seam.annotations.intercept.Interceptor;

@SuppressWarnings("serial")
@Interceptor
public class BooleanInterceptor implements Serializable {

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        Object result = ctx.proceed();
        if (result instanceof Boolean) {
            return !(Boolean) result;
        }
        return result;
    }

}
