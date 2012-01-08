//$Id: Interceptor.java 10311 2009-04-06 16:14:09Z pete.muir@jboss.org $
package org.jboss.seam.intercept;

import org.jboss.seam.annotations.intercept.InterceptorType;

/**
 * Wraps and delegates to a Seam interceptor.
 * 
 * @author Gavin King
 */
public interface Interceptor {
    public Object createUserInterceptor();

    public Class getUserInterceptorClass();

    public InterceptorType getType();

    public boolean isOptimized();

    public Object aroundInvoke(InvocationContext invocation, Object userInterceptor) throws Exception;

    public Object postConstruct(InvocationContext invocation, Object userInterceptor) throws Exception;

    public Object preDestroy(InvocationContext invocation, Object userInterceptor) throws Exception;

    public Object prePassivate(InvocationContext invocation, Object userInterceptor) throws Exception;

    public Object postActivate(InvocationContext invocation, Object userInterceptor) throws Exception;

    /**
     * Return true if the interceptor should be enabled for the component instance
     * 
     * Should only be called during deployment
     */
    public boolean isInterceptorEnabled();
}
