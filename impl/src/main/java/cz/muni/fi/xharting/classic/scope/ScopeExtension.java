package cz.muni.fi.xharting.classic.scope;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

import org.jboss.seam.annotations.Conversational;
import org.jboss.solder.core.Veto;
import org.jboss.solder.reflection.annotated.AnnotatedTypeBuilder;

import cz.muni.fi.xharting.classic.bijection.OutjectedReferenceHolder;
import cz.muni.fi.xharting.classic.scope.page.PageContext;
import cz.muni.fi.xharting.classic.scope.page.PageScoped;
import cz.muni.fi.xharting.classic.scope.stateless.StatelessContext;
import cz.muni.fi.xharting.classic.scope.stateless.StatelessScoped;
import cz.muni.fi.xharting.classic.util.ScopeUtils;

/**
 * Registers scopes and extensions provided by Classic.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 */
public class ScopeExtension implements Extension {

    private final List<Class<? extends Annotation>> statefulScopes;

    public ScopeExtension() {
        List<Class<? extends Annotation>> statefulScopesBuilder = new LinkedList<Class<? extends Annotation>>();
        statefulScopesBuilder = new ArrayList<Class<? extends Annotation>>();
        statefulScopesBuilder.add(RequestScoped.class);
        statefulScopesBuilder.add(PageScoped.class);
        statefulScopesBuilder.add(ConversationScoped.class);
        statefulScopesBuilder.add(SessionScoped.class);
        statefulScopesBuilder.add(ApplicationScoped.class);
        statefulScopes = Collections.unmodifiableList(statefulScopesBuilder);
    }

    public List<Class<? extends Annotation>> getStatefulScopes() {
        return statefulScopes;
    }

    void registerConversational(@Observes BeforeBeanDiscovery event)
    {
        event.addInterceptorBinding(Conversational.class);
    }
    
    void registerScopes(@Observes BeforeBeanDiscovery event) {
        event.addScope(StatelessScoped.class, true, false);
        event.addScope(PageScoped.class, true, true);
    }

    void registerContexts(@Observes AfterBeanDiscovery event, BeanManager manager) {
        event.addContext(new StatelessContext());
        event.addContext(new PageContext());
    }

    void registerOutjectedReferenceHolders(@Observes BeforeBeanDiscovery event, BeanManager manager) {
        for (Class<? extends Annotation> scope : getStatefulScopes()) {
            event.addAnnotatedType(createOutjectedReferenceHolder(scope));
        }
    }

    private AnnotatedType<?> createOutjectedReferenceHolder(Class<? extends Annotation> scope) {
        AnnotatedTypeBuilder<OutjectedReferenceHolder> builder = new AnnotatedTypeBuilder<OutjectedReferenceHolder>();
        builder.readFromType(OutjectedReferenceHolder.class);
        builder.addToClass(ScopeUtils.getScopeLiteral(scope));
        builder.addToClass(OutjectedReferenceHolder.ScopeQualifier.ScopeQualifierLiteral.valueOf(scope));
        builder.removeFromClass(Veto.class);
        return builder.create();
    }
}
