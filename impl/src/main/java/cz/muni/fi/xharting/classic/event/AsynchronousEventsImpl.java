package cz.muni.fi.xharting.classic.event;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.event.Event;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;

import org.jboss.seam.async.Schedule;
import org.jboss.seam.async.TimerSchedule;
import org.jboss.seam.core.Events;

/**
 * Stateless session bean that handles asynchronous delivery of events.
 * 
 * @author Jozef Hartinger
 * 
 */
@Stateless
public class AsynchronousEventsImpl {

    @Inject
    private Events events;
    @Inject
    private Event<EventPayload> event;
    @Resource
    private TimerService timerService;

    @Asynchronous
    public void raiseAsynchronousEvent(String type, Object... parameters) {
        events.raiseEvent(type, parameters);
    }

    public void scheduleTimedEvent(String type, Schedule schedule, Object... parameters) {
        EventPayload payload = new EventPayload(type, parameters);
        TimerConfig timerConfig = new TimerConfig(payload, false);

        if (schedule instanceof TimerSchedule) {
            TimerSchedule ts = (TimerSchedule) schedule;
            if (ts.getDuration() != null && ts.getExpiration() == null && ts.getFinalExpiration() == null
                    && ts.getIntervalDuration() == null) {
                timerService.createSingleActionTimer(ts.getDuration(), timerConfig);
                return;
            } else if (ts.getDuration() == null && ts.getExpiration() != null && ts.getFinalExpiration() == null
                    && ts.getIntervalDuration() == null) {
                timerService.createSingleActionTimer(ts.getExpiration(), timerConfig);
                return;
            } else if (ts.getDuration() != null && ts.getExpiration() == null && ts.getFinalExpiration() == null
                    && ts.getIntervalDuration() != null) {
                timerService.createIntervalTimer(ts.getDuration(), ts.getIntervalDuration(), timerConfig);
                return;
            } else if (ts.getDuration() == null && ts.getExpiration() != null && ts.getFinalExpiration() == null
                    && ts.getIntervalDuration() != null) {
                timerService.createIntervalTimer(ts.getExpiration(), ts.getIntervalDuration(), timerConfig);
                return;
            }
        } else {
            if (schedule.getDuration() != null && schedule.getExpiration() == null && schedule.getFinalExpiration() == null) {
                timerService.createSingleActionTimer(schedule.getDuration(), timerConfig);
                return;
            } else if (schedule.getDuration() == null && schedule.getExpiration() != null
                    && schedule.getFinalExpiration() == null) {
                timerService.createSingleActionTimer(schedule.getExpiration(), timerConfig);
                return;
            }
        }
        throw new UnsupportedOperationException("Schedule object " + schedule + " not supported.");
    }

    @Timeout
    void timedEventCallback(Timer timer) {
        if (timer.getInfo() instanceof EventPayload) {
            EventPayload payload = (EventPayload) timer.getInfo();
            event.select(new EventQualifier.EventQualifierLiteral(payload.getName(), TransactionPhase.IN_PROGRESS)).fire(
                    payload);
        }
    }
}
