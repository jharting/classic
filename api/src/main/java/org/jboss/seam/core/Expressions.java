package org.jboss.seam.core;

import java.io.Serializable;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

import org.jboss.seam.util.StaticLookup;

/**
 * Factory for EL method and value expressions.
 * 
 * This default implementation uses JBoss EL.
 * 
 * @author Gavin King
 * @author Jozef Hartinger
 */
//@BypassInterceptors
//@Install(precedence=BUILT_IN)
//@Name("org.jboss.seam.core.expressions")
public abstract class Expressions implements Serializable
{
   private static final long serialVersionUID = -8104543331682592706L;

   public abstract ExpressionFactory getExpressionFactory();
   
   /**
    * Get an appropriate ELContext. If there is an active JSF request,
    * use JSF's ELContext. Otherwise, use one that we create.
    */
   public abstract ELContext getELContext();

   /**
    * Create a value expression.
    * 
    * @param expression a JBoss EL value expression
    */
   public abstract ValueExpression<Object> createValueExpression(String expression);
   
   /**
    * Create a method expression.
    * 
    * @param expression a JBoss EL method expression
    */
   public abstract MethodExpression<Object> createMethodExpression(String expression);
   
   /**
    * Create a value expression.
    * 
    * @param expression a JBoss EL value expression
    * @param type the type of the value 
    */
   public abstract <T> ValueExpression<T> createValueExpression(final String expression, final Class<T> type);
   
   /**
    * Create a method expression.
    * 
    * @param expression a JBoss EL method expression
    * @param type the method return type
    * @param argTypes the method parameter types
    */
   @SuppressWarnings("rawtypes")
   public abstract <T> MethodExpression<T> createMethodExpression(final String expression, final Class<T> type, final Class... argTypes);
   
   /**
    * A value expression - an EL expression that evaluates to
    * an attribute getter or get/set pair. This interface
    * is just a genericized version of the Unified EL ValueExpression
    * interface.
    * 
    * @author Gavin King
    *
    * @param <T> the type of the value
    */
   public static interface ValueExpression<T> extends Serializable
   {
      public T getValue();
      public void setValue(T value);
      public String getExpressionString();
      public Class<T> getType();
      /**
       * @return the underlying Unified EL ValueExpression
       */
      public javax.el.ValueExpression toUnifiedValueExpression();
   }
   
   /**
    * A method expression - an EL expression that evaluates to
    * a method. This interface is just a genericized version of 
    * the Unified EL ValueExpression interface.
    * 
    * @author Gavin King
    *
    * @param <T> the method return type
    */
   public static interface MethodExpression<T> extends Serializable
   {
      public T invoke(Object... args);
      public String getExpressionString();
      /**
       * @return the underlying Unified EL MethodExpression
       */
      public javax.el.MethodExpression toUnifiedMethodExpression();
   }
   
   public static Expressions instance()
   {
       return StaticLookup.lookupBean(Expressions.class);
   }
}
