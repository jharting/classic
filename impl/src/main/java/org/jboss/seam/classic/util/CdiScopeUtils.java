package org.jboss.seam.classic.util;

import java.lang.annotation.Annotation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;

import org.jboss.seam.ScopeType;
import org.jboss.solder.literal.ApplicationScopedLiteral;
import org.jboss.solder.literal.ConversationScopedLiteral;
import org.jboss.solder.literal.DependentLiteral;
import org.jboss.solder.literal.RequestScopedLiteral;
import org.jboss.solder.literal.SessionScopedLiteral;

public class CdiScopeUtils {

    private CdiScopeUtils() {
    }

    public static Annotation getScopeLiteral(Class<? extends Annotation> clazz)
    {
        if (RequestScoped.class.equals(clazz))
        {
            return RequestScopedLiteral.INSTANCE;
        }
        if (ConversationScoped.class.equals(clazz))
        {
            return ConversationScopedLiteral.INSTANCE;
        }
        if (SessionScoped.class.equals(clazz))
        {
            return SessionScopedLiteral.INSTANCE;
        }
        if (ApplicationScoped.class.equals(clazz))
        {
            return ApplicationScopedLiteral.INSTANCE;
        }
        if (Dependent.class.equals(clazz))
        {
            return DependentLiteral.INSTANCE;
        }
        throw new IllegalArgumentException("Unknown scope: " + clazz.getName());
    }
    
    public static ScopeType transformExplicitCdiScopeToLegacyScope(Class<? extends Annotation> scope) {
        if (RequestScoped.class.equals(scope)) {
            return ScopeType.EVENT;
        } else if (ConversationScoped.class.equals(scope)) {
            return ScopeType.CONVERSATION;
        } else if (SessionScoped.class.equals(scope)) {
            return ScopeType.SESSION;
        } else if (ApplicationScoped.class.equals(scope)) {
            return ScopeType.APPLICATION;
        } else {
            throw new IllegalArgumentException("Unsupported scope " + scope);
        }
    }
}
