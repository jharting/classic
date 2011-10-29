//$Id: Conversational.java 5278 2007-06-19 19:02:37Z gavin $
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that a component or method is conversational, 
 * and may only be called inside the scope of a long-running 
 * conversation.
 * 
 * @author Gavin King
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented
@Inherited
public @interface Conversational {}
