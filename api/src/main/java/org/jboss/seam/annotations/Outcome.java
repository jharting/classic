//$Id: Outcome.java 5516 2007-06-25 17:51:16Z gavin $
package org.jboss.seam.annotations;

@Deprecated
public class Outcome
{
   /**
    * Annotations may not specify a null String. This
    * value lets us specify a null outcome in an
    * annotation such as @Begin(ifOutcome=...).
    * 
    * @deprecated
    */
   public static final String REDISPLAY = "org.jboss.seam.outcome.null";
}
