package org.jboss.seam.classic.util;

import java.lang.annotation.Annotation;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.persistence.Entity;

import org.jboss.seam.ScopeType;
import org.jboss.seam.classic.init.metadata.BeanDescriptor;
import org.jboss.seam.classic.init.metadata.RoleDescriptor;

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

    /**
     * Translates Seam 2 ScopeType to matching CDI scope. Default scope rules for Seam 2 beans are considered.
     */
    public static Class<? extends Annotation> transformLegacyBeanScopeToCdiScope(ScopeType scope, Class<?> javaClass) {
        if (scope.equals(ScopeType.UNSPECIFIED)) {
            if (javaClass.isAnnotationPresent(Stateful.class) || javaClass.isAnnotationPresent(Entity.class)) {
                return ConversationScoped.class;
            } else if (javaClass.isAnnotationPresent(Stateless.class)) {
                return Dependent.class;
            } else {
                return RequestScoped.class;
            }
        } else {
            return transformExplicitLegacyScopeToCdiScope(scope);
        }
    }

    /**
     * Translates Seam 2 ScopeType to matching CDI scope. Default scope rules for Seam 2 factories are considered.
     */
    public static Class<? extends Annotation> transformLegacyFactoryScopeToCdiScope(ScopeType scope, ScopeType hostScope, Class<?> hostClass) {
        if (scope.equals(ScopeType.UNSPECIFIED)) {
            if (hostScope.equals(ScopeType.STATELESS)) {
                return RequestScoped.class;
            } else {
                return transformLegacyBeanScopeToCdiScope(hostScope, hostClass);
            }
        } else {
            return transformExplicitLegacyScopeToCdiScope(scope);
        }
    }
    
    /**
     * Translates Seam 2 ScopeType to matching CDI scope. Default scope rules for Seam 2 outjected fields are considered. 
     */
    public static Class<? extends Annotation> transformLegacyOutScopeToCdiScope(ScopeType specifiedScope, BeanDescriptor descriptor)
    {
        if (specifiedScope == ScopeType.UNSPECIFIED)
        {
            if (descriptor.getRoles().size() != 1)
            {
                throw new IllegalStateException("Outjection scope not specified explicitly on multi-role bean.");
            }
            RoleDescriptor role = descriptor.getRoles().iterator().next();
            ScopeType scope = role.getScope();
            if (ScopeType.STATELESS == scope)
            {
                scope = ScopeType.EVENT;
            }
            return ClassicScopeUtils.transformLegacyBeanScopeToCdiScope(scope, descriptor.getJavaClass());
        }
        return transformExplicitLegacyScopeToCdiScope(specifiedScope);
    }
}
