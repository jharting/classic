package cz.muni.fi.xharting.classic.synchronization;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.LockTimeoutException;

/**
 * Serializes calls to a component.
 * 
 * @author Gavin King
 * @author Jozef Hartinger
 */
@Interceptor
@Synchronized
public class SynchronizationInterceptor implements Serializable {

    private static final long serialVersionUID = -5701898667462097519L;
    private ReentrantLock lock = new ReentrantLock(true);

    @Inject
    private Conversation conversation;

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        long timeout = getTimeout(ctx.getTarget().getClass(), ctx.getMethod());
        if (lock.tryLock(timeout, TimeUnit.MILLISECONDS)) {
            try {
                return ctx.proceed();
            } finally {
                lock.unlock();
            }
        } else {
            throw new LockTimeoutException("could not acquire lock on @Synchronized component: " + ctx.getTarget().toString());
        }
    }

    private long getTimeout(Class<?> clazz, Method method) {
        if (conversation.getConcurrentRequestTimeout() != null && conversation.getConcurrentRequestTimeout() != Synchronized.DEFAULT_TIMEOUT) {
            return conversation.getConcurrentRequestTimeout();
        }
        if (clazz.isAnnotationPresent(Synchronized.class)) {
            return clazz.getAnnotation(Synchronized.class).timeout();
        }
        if (method.isAnnotationPresent(Synchronized.class)) {
            return method.getAnnotation(Synchronized.class).timeout();
        }
        return Synchronized.DEFAULT_TIMEOUT;
    }
}
