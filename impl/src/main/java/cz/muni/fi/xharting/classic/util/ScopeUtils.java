package cz.muni.fi.xharting.classic.util;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.BeanManager;

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

    // TODO
//    public static ScopeComparator getScopeComparator(BeanManager manager) {
//
//    }

    private static class ScopeComparator implements Comparator<Class<? extends Annotation>> {

        private List<Class<? extends Annotation>> scopes;

        @Override
        public int compare(Class<? extends Annotation> o1, Class<? extends Annotation> o2) {
            int scope1 = scopes.indexOf(o1);
            int scope2 = scopes.indexOf(o2);
            return scope1 - scope2;
        }

    }
}
