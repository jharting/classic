//$Id: InstantiationException.java 3833 2007-02-13 06:02:53Z gavin $
package org.jboss.seam;

/**
 * Thrown when Seam cannot instantiate a component.
 * 
 * @author Gavin King
 *
 */
public class InstantiationException extends RuntimeException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -5437284703511833879L;

   public InstantiationException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public InstantiationException(Throwable cause)
   {
      super(cause);
   }

}
