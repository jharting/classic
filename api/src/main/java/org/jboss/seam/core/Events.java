package org.jboss.seam.core;

import org.jboss.seam.async.Schedule;
import org.jboss.seam.util.StaticLookup;



/**
 * Support for Seam component-driven events
 * 
 * @author Gavin King
 * @author Jozef Hartinger
 *
 */
public abstract class Events 
{
   
   /**
    * Add a new listener for a given event type
    * 
    * @param type the event type
    * @param methodBindingExpression a method binding, expressed in EL
    * @param argTypes the argument types of the method binding
    */
   public abstract void addListener(String type, String methodBindingExpression, @SuppressWarnings("rawtypes") Class... argTypes);
   
   /**
    * Raise an event that is to be processed synchronously
    * 
    * @param type the event type
    * @param parameters parameters to be passes to the listener method
    */
   public abstract void raiseEvent(String type, Object... parameters);
   
   /**
    * Raise an event that is to be processed asynchronously
    * 
    * @param type the event type
    * @param parameters parameters to be passes to the listener method
    */
   public abstract void raiseAsynchronousEvent(String type, Object... parameters);

   /**
    * Raise an event that is to be processed according to a "schedule"
    * 
    * @see TimerSchedule (EJB, quartz or JDK timer service)
    * @see CronSchedule (quartz timer service only)
    * 
    * @param type the event type
    * @param schedule the schedule object, specific to the dispatcher strategy
    * @param parameters parameters to be passes to the listener method
    */
   public abstract void raiseTimedEvent(String type, Schedule schedule, Object... parameters);
   
   /**
    * Raise an event that is to be processed after successful completion of 
    * the current transaction
    * 
    * @param type the event type
    * @param parameters parameters to be passes to the listener method
    */
   public abstract void raiseTransactionSuccessEvent(String type, Object... parameters);
   
   /**
    * Raise an event that is to be processed after the current transaction
    * ends
    * 
    * @param type the event type
    * @param parameters parameters to be passes to the listener method
    */
   public abstract void raiseTransactionCompletionEvent(String type, Object... parameters);
   
   public static boolean exists()
   {
       return true;
   }

   public static Events instance()
   {
	   return StaticLookup.lookupBean(Events.class);
   }
}

