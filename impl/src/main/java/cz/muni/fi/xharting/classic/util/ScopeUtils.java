package cz.muni.fi.xharting.classic.util;

import java.lang.annotation.Annotation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;

import org.jboss.solder.literal.ApplicationScopedLiteral;
import org.jboss.solder.literal.ConversationScopedLiteral;
import org.jboss.solder.literal.DependentLiteral;
import org.jboss.solder.literal.RequestScopedLiteral;
import org.jboss.solder.literal.SessionScopedLiteral;

import cz.muni.fi.xharting.classic.scope.page.PageScoped;
import cz.muni.fi.xharting.classic.scope.stateless.StatelessScoped;

public class ScopeUtils {

    private ScopeUtils() {
    }

    public static Annotation getScopeLiteral(Class<? extends Annotation> clazz) {
        if (RequestScoped.class.equals(clazz)) {
            return RequestScopedLiteral.INSTANCE;
        }
        if (ConversationScoped.class.equals(clazz)) {
            return ConversationScopedLiteral.INSTANCE;
        }
        if (SessionScoped.class.equals(clazz)) {
            return SessionScopedLiteral.INSTANCE;
        }
        if (ApplicationScoped.class.equals(clazz)) {
            return ApplicationScopedLiteral.INSTANCE;
        }
        if (Dependent.class.equals(clazz)) {
            return DependentLiteral.INSTANCE;
        }
        if (StatelessScoped.class.equals(clazz)) {
            return StatelessScoped.StatelessScopedLiteral.INSTANCE;
        }
        if (PageScoped.class.equals(clazz)) {
            return PageScoped.PageScopedLiteral.INSTANCE;
        }
        throw new IllegalArgumentException("Unknown scope: " + clazz.getName());
    }
}
