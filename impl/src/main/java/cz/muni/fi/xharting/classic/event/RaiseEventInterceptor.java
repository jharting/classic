package cz.muni.fi.xharting.classic.event;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.seam.annotations.RaiseEvent;

/**
 * Fires events upon successful execution of a {@link RaiseEvent}-annotated method.
 * 
 * @author Jozef Hartinger
 * 
 */
@Interceptor
@RaiseEvent
public class RaiseEventInterceptor {

    @Inject
    private EventsImpl events;

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        Object result = ctx.proceed();

        if (result != null || ctx.getMethod().getReturnType().equals(void.class)) {
            RaiseEvent annotation = ctx.getMethod().getAnnotation(RaiseEvent.class);
            if (annotation == null) {
                throw new IllegalStateException("Unable to find @RaiseEvent annotation.");
            }

            if (annotation.value().length == 0) {
                events.raiseEvent(ctx.getMethod().getName());
            } else {
                for (String eventId : annotation.value()) {
                    events.raiseEvent(eventId);
                }
            }
        }
        return result;
    }
}
