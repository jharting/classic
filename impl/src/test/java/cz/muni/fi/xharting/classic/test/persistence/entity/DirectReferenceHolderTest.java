package cz.muni.fi.xharting.classic.test.persistence.entity;

import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import cz.muni.fi.xharting.classic.util.reference.PassivationCapableDirectReferenceProducer;

@RunWith(Arquillian.class)
public class DirectReferenceHolderTest {

    @Inject
    private BeanManager manager;

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", SerializableEntity.class, InjectedBean.class, NonSerializableEntity.class);
    }

    /**
     * Verifies that when we inject an entity we get an unwrapped instance which can be passed to
     * {@link EntityManager#persist(Object)}.
     */
    @Test
    public void testEntityCanBeInjectedAndPersisted(@Named("injectedBean") InjectedBean injectedBean) {
        assertEquals(SerializableEntity.class, injectedBean.getEntity().getClass());
        assertEquals(SerializableEntity.class, injectedBean.getEntity2().getClass());
        assertEquals(NonSerializableEntity.class, injectedBean.getEntity3().getClass());
    }

    @Test
    public void testCorrectBeanProducerRegistered() {
        assertTrue(manager.resolve(manager.getBeans("entity")) instanceof PassivationCapableDirectReferenceProducer);
        assertTrue(manager.resolve(manager.getBeans("entity2")) instanceof PassivationCapableDirectReferenceProducer);
        assertFalse(manager.resolve(manager.getBeans("entity3")) instanceof PassivationCapableDirectReferenceProducer);
    }

}
