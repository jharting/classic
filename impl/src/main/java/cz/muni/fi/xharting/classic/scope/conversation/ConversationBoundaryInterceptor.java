package cz.muni.fi.xharting.classic.scope.conversation;

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.ConversationBoundary;
import org.jboss.seam.annotations.End;
import org.jboss.seam.core.Conversation;
import org.jboss.solder.reflection.AnnotationInspector;

/**
 * Interceptor that starts/ends a long-running conversation if a {@link Begin} or {@link End} annotation is used on the
 * intercepted method.
 * 
 * @author Jozef Hartinger
 * 
 */
@Interceptor
@ConversationBoundary
public class ConversationBoundaryInterceptor {

    @Inject
    private BeanManager manager;
    @Inject
    private Conversation conversation;

    @AroundInvoke
    Object intercept(InvocationContext ctx) throws Exception {
        Object result = ctx.proceed();

        Begin begin = AnnotationInspector.getAnnotation(ctx.getMethod(), Begin.class, manager);
        if (begin != null && outcomeMatches(ctx.getMethod(), result, begin.ifOutcome())) {
            conversation.begin(begin.join(), begin.nested());
        }
        End end = AnnotationInspector.getAnnotation(ctx.getMethod(), End.class, manager);
        if (end != null && outcomeMatches(ctx.getMethod(), result, end.ifOutcome())) {
            conversation.end(end.beforeRedirect());
        }

        return result;
    }

    private boolean outcomeMatches(Method method, Object outcome, String[] expectedOutcomes) {
        if (outcome == null && !void.class.equals(method.getReturnType())) {
            return false;
        }
        if (expectedOutcomes.length == 0) {
            return true;
        }
        if (outcome instanceof String) {
            for (String expectedOutcome : expectedOutcomes) {
                if (expectedOutcome.equals(outcome)) {
                    return true;
                }
            }
        }
        return false;
    }
}
