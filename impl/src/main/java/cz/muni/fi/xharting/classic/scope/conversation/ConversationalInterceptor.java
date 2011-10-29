package cz.muni.fi.xharting.classic.scope.conversation;

import java.io.Serializable;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.seam.NoConversationException;
import org.jboss.seam.annotations.Conversational;

import cz.muni.fi.xharting.classic.event.EventsImpl;
import cz.muni.fi.xharting.classic.util.CdiUtils;

@Interceptor
@Conversational
public class ConversationalInterceptor implements Serializable {

    private static final long serialVersionUID = 5363536727813095400L;
    
    @Inject
    private Conversation conversation;
    @Inject
    private EventsImpl event;
    @Inject
    private BeanManager manager;

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        if (CdiUtils.isContextActive(ConversationScoped.class, manager) && !conversation.isTransient()) {
            return ctx.proceed();
        }
        event.raiseEvent("org.jboss.seam.noConversation");
        throw new NoConversationException("no long-running conversation for @Conversational bean: "
                + ctx.getTarget().toString());
    }
}
