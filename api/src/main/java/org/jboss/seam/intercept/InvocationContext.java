package org.jboss.seam.intercept;


/**
 * A copy of the EE5 standard InvocationContext API.
 * We do this because some poor souls are still using
 * J2EE. Pray for them.
 * 
 * @author Gavin King
 *
 */
@Deprecated
public interface InvocationContext extends javax.interceptor.InvocationContext
{
}
