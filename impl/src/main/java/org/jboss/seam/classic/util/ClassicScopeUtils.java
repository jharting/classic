package org.jboss.seam.classic.util;

import java.lang.annotation.Annotation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;

import org.jboss.seam.ScopeType;

public class ClassicScopeUtils {

    /**
     * Translates Seam 2 ScopeType to matching CDI scope.
     */
    public static Class<? extends Annotation> transformExplicitLegacyScopeToCdiScope(ScopeType scope) {
        switch (scope) {
            case STATELESS:
                return Dependent.class;
            case METHOD:
                throw new UnsupportedOperationException("Scope not supported"); // TODO
            case EVENT:
                return RequestScoped.class;
            case PAGE:
                throw new UnsupportedOperationException("Scope not supported"); // TODO
            case CONVERSATION:
                return ConversationScoped.class;
            case SESSION:
                return SessionScoped.class;
            case APPLICATION:
                return ApplicationScoped.class;
            case BUSINESS_PROCESS:
                throw new UnsupportedOperationException("Scope not supported"); // TODO
            default:
                throw new IllegalArgumentException("Not an explicit scope " + scope);
        }

    }
}
