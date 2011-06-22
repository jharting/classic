//$Id: Create.java 5262 2007-06-18 21:32:04Z gavin $
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Alternative to javax.annotations.PostConstruct
 * for use in a pre Java EE 5 environment.
 * 
 * Designates a create method that is called after
 * instantiation of a component.
 * 
 * @author Gavin King
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
@Inherited
public @interface Create {}
