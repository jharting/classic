package org.jboss.seam.classic.test.intercept;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.seam.annotations.intercept.Interceptor;
import org.jboss.seam.annotations.intercept.InterceptorType;

@Interceptor(around = BooleanInterceptor.class, stateless = true, type = InterceptorType.CLIENT)
public class IntegerInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        Object result = ctx.proceed();
        if (result instanceof Integer) {
            return 10 + (Integer) result;
        }
        return result;
    }
}
