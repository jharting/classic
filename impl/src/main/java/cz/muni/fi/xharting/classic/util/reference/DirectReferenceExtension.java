package cz.muni.fi.xharting.classic.util.reference;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;

import org.jboss.solder.reflection.Synthetic;
import org.jboss.solder.reflection.annotated.AnnotatedTypeBuilder;
import org.jboss.solder.reflection.annotated.AnnotationBuilder;
import org.jboss.solder.reflection.annotated.AnnotationRedefiner;
import org.jboss.solder.reflection.annotated.RedefinitionContext;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import cz.muni.fi.xharting.classic.util.Annotations;

/**
 * The extension scans for {@link DirectReference} occurences on bean definitions (including producer methods and fields),
 * suppresses such beans by using the synthetic qualifier and register {@link DirectReferenceHolder} and
 * {@link DirectReferenceProducer} for each such bean.
 * 
 * @author Jozef Hartinger
 * 
 */
public class DirectReferenceExtension implements Extension {

    private BeanManager manager;
    private DirectReferenceRedefiner redefiner = new DirectReferenceRedefiner();

    private final SetMultimap<Synthetic, Annotation> qualifiers = HashMultimap.create();
    private final Map<Synthetic, Class<? extends Annotation>> scopes = new HashMap<Synthetic, Class<? extends Annotation>>();

    public List<Bean<?>> beansToRegister = new LinkedList<Bean<?>>();

    void getManager(@Observes BeforeBeanDiscovery event, BeanManager manager) {
        this.manager = manager;
    }

    <T> void scanDirectReferenceDeclaringTypes(@Observes ProcessAnnotatedType<T> event, BeanManager manager) {
        if (requiresDirectReference(event.getAnnotatedType())) {
            AnnotatedType<T> type = event.getAnnotatedType();
            AnnotatedTypeBuilder<T> builder = new AnnotatedTypeBuilder<T>().readFromType(type);
            builder.redefine(DirectReference.class, redefiner);
            event.setAnnotatedType(builder.create());
        }
    }

    /**
     * Performance optimalization.
     */
    public boolean requiresDirectReference(AnnotatedType<?> type) {
        if (type.isAnnotationPresent(DirectReference.class)) {
            return true;
        }
        for (AnnotatedField<?> field : type.getFields()) {
            if (field.isAnnotationPresent(DirectReference.class)) {
                return true;
            }
        }
        for (AnnotatedMethod<?> method : type.getMethods()) {
            if (method.isAnnotationPresent(DirectReference.class)) {
                return true;
            }
        }
        return false;
    }

    public class DirectReferenceRedefiner implements AnnotationRedefiner<DirectReference> {

        public void redefine(RedefinitionContext<DirectReference> ctx) {
            AnnotationBuilder builder = ctx.getAnnotationBuilder();
            // process scope
            Class<? extends Annotation> scope = getScope(ctx.getAnnotatedElement());
            if (scope == null) {
                throw new IllegalArgumentException(ctx.getAnnotatedElement() + " does not declare a normal scope.");
            }
            builder.remove(scope);

            // process qualifiers
            Set<Annotation> qualifiers = Annotations.getQualifiers(ctx.getAnnotatedElement(), manager);
            for (Annotation qualifier : qualifiers) {
                builder.remove(qualifier.annotationType());
            }
            Synthetic synthetic = DirectReferenceFactory.syntheticProvider.get();
            builder.add(synthetic);

            scopes.put(synthetic, scope);
            DirectReferenceExtension.this.qualifiers.putAll(synthetic, qualifiers);
        }

        private Class<? extends Annotation> getScope(AnnotatedElement annotated) {
            for (Annotation annotation : annotated.getAnnotations()) {
                if (manager.isNormalScope(annotation.annotationType())) {
                    return annotation.annotationType();
                }
            }
            return null;
        }
    }

    void processDirectReferenceBeans(@Observes ProcessBean<?> event) {
        Synthetic synthetic = Annotations.getAnnotation(event.getBean().getQualifiers(), Synthetic.class);
        if (synthetic != null && DirectReferenceFactory.NAMESPACE.equals(synthetic.namespace())) {
            Bean<?> bean = event.getBean();
            beansToRegister.addAll(DirectReferenceFactory.createDirectReferenceHolder(bean.getBeanClass(), bean.getTypes(), qualifiers.get(synthetic), null, synthetic,
                    scopes.get(synthetic), manager, true));
        }
    }

    public void registerHoldersAndProducers(@Observes AfterBeanDiscovery event) {
        for (Bean<?> bean : beansToRegister) {
            event.addBean(bean);
        }
    }
}
