package cz.muni.fi.xharting.classic.event;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.async.Schedule;
import org.jboss.seam.core.Events;

/**
 * Implementation of the legacy {@link Events} API.
 * 
 * @author Jozef Hartinger
 * 
 */
@Named("org.jboss.seam.core.events")
@ApplicationScoped
public class EventsImpl extends Events {

    @Inject
    private Event<EventPayload> event;
    @Inject
    private AsynchronousEventsImpl async;

    /**
     * Raise an event that is to be processed synchronously
     * 
     * @param type the event type
     * @param parameters parameters to be passes to the listener method
     */
    public void raiseEvent(String type, Object... parameters) {
        event.select(new EventQualifier.EventQualifierLiteral(type, TransactionPhase.IN_PROGRESS)).fire(new EventPayload(type, parameters));
    }

    @Override
    public void addListener(String type, String methodBindingExpression, @SuppressWarnings("rawtypes") Class... argTypes) {
        throw new UnsupportedOperationException("Unable to register observer methods at runtime.");
    }

    @Override
    public void raiseAsynchronousEvent(String type, Object... parameters) {
        async.raiseAsynchronousEvent(type, parameters);
    }

    @Override
    public void raiseTimedEvent(String type, Schedule schedule, Object... parameters) {
        async.scheduleTimedEvent(type, schedule, parameters);
    }

    @Override
    public void raiseTransactionSuccessEvent(String type, Object... parameters) {
        event.select(new EventQualifier.EventQualifierLiteral(type, TransactionPhase.AFTER_SUCCESS)).fire(new EventPayload(type, parameters));
    }

    @Override
    public void raiseTransactionCompletionEvent(String type, Object... parameters) {
        event.select(new EventQualifier.EventQualifierLiteral(type, TransactionPhase.AFTER_COMPLETION)).fire(new EventPayload(type, parameters));
    }

}
