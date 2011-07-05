package org.jboss.seam.classic.init;

import java.lang.annotation.Annotation;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.persistence.Entity;

import org.jboss.seam.ScopeType;
import org.jboss.seam.solder.literal.ApplicationScopedLiteral;
import org.jboss.seam.solder.literal.ConversationScopedLiteral;
import org.jboss.seam.solder.literal.DependentLiteral;
import org.jboss.seam.solder.literal.RequestScopedLiteral;
import org.jboss.seam.solder.literal.SessionScopedLiteral;

public class ScopeUtils {

    public static Annotation transformExplicitScope(ScopeType scope) {
        switch (scope) {
            case STATELESS:
                return DependentLiteral.INSTANCE;
            case METHOD:
                throw new UnsupportedOperationException("Scope not supported"); // TODO
            case EVENT:
                return RequestScopedLiteral.INSTANCE;
            case PAGE:
                throw new UnsupportedOperationException("Scope not supported"); // TODO
            case CONVERSATION:
                return ConversationScopedLiteral.INSTANCE;
            case SESSION:
                return SessionScopedLiteral.INSTANCE;
            case APPLICATION:
                return ApplicationScopedLiteral.INSTANCE;
            case BUSINESS_PROCESS:
                throw new UnsupportedOperationException("Scope not supported"); // TODO
            default:
                throw new IllegalArgumentException("Not an explicit scope");
        }

    }

    public static Annotation transformBeanScope(ScopeType scope, Class<?> javaClass) {
        if (scope.equals(ScopeType.UNSPECIFIED))
        {
            if (javaClass.isAnnotationPresent(Stateful.class) || javaClass.isAnnotationPresent(Entity.class)) {
                return ConversationScopedLiteral.INSTANCE;
            } else if (javaClass.isAnnotationPresent(Stateless.class)) {
                return DependentLiteral.INSTANCE;
            } else {
                return RequestScopedLiteral.INSTANCE;
            }
        }
        else
        {
            return transformExplicitScope(scope);
        }
    }
    
    public static Annotation transformFactoryScope(ScopeType scope, ScopeType hostScope, Class<?> hostClass) {
        if (scope.equals(ScopeType.UNSPECIFIED))
        {
            if (hostScope.equals(ScopeType.STATELESS))
            {
                return RequestScopedLiteral.INSTANCE;
            }
            else
            {
                return transformBeanScope(hostScope, hostClass);
            }
        }
        else
        {
            return transformExplicitScope(scope);
        }
    }
}
