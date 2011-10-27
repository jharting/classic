//$Id: BypassInterceptors.java 5355 2007-06-20 20:42:22Z gavin $
package org.jboss.seam.annotations.intercept;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Disables interception of calls to a Seam component
 * or Seam component method.
 * 
 * @author Gavin King
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented
public @interface BypassInterceptors {}
