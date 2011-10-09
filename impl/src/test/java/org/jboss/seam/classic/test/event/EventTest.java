package org.jboss.seam.classic.test.event;

import static org.jboss.seam.classic.test.utils.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.LinkedList;

import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.async.Schedule;
import org.jboss.seam.classic.init.event.EventPayload;
import org.jboss.seam.classic.init.event.EventQualifier;
import org.jboss.seam.core.Events;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class EventTest {

    @Inject
    private ObservingBean observingBean;

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war").addPackage(EventTest.class.getPackage());
    }

    @Test
    public void testEventsDispatched() {
    	Events events = Events.instance();
        observingBean.reset();
        events.raiseEvent("foo");
        assertEquals(1, observingBean.getFooObserverCalled());
        assertEquals(1, observingBean.getFooBarObserverCalled());
        assertEquals(0, observingBean.getBarObserverCalled());
        assertEquals(0, observingBean.getBazObserverCalled());

        observingBean.reset();
        events.raiseEvent("bar");
        assertEquals(0, observingBean.getFooObserverCalled());
        assertEquals(1, observingBean.getFooBarObserverCalled());
        assertEquals(1, observingBean.getBarObserverCalled());
        assertEquals(0, observingBean.getBazObserverCalled());

        observingBean.reset();
        events.raiseTransactionCompletionEvent("foo");
        assertEquals(1, observingBean.getFooObserverCalled());
        assertEquals(1, observingBean.getFooBarObserverCalled());
        assertEquals(0, observingBean.getBarObserverCalled());
        assertEquals(0, observingBean.getBazObserverCalled());

        observingBean.reset();
        events.raiseTransactionSuccessEvent("foo");
        assertEquals(1, observingBean.getFooObserverCalled());
        assertEquals(1, observingBean.getFooBarObserverCalled());
        assertEquals(0, observingBean.getBarObserverCalled());
        assertEquals(0, observingBean.getBazObserverCalled());
    }

    @Test
    public void testRaiseEventInterceptor(EventRaisingBean bean) {
        observingBean.reset();
        bean.foo();
        assertEquals(1, observingBean.getFooObserverCalled());
        assertEquals(1, observingBean.getFooBarObserverCalled());
        assertEquals(0, observingBean.getBarObserverCalled());
        
        observingBean.reset();
        bean.fooBar();
        assertEquals(1, observingBean.getFooObserverCalled());
        assertEquals(2, observingBean.getFooBarObserverCalled());
        assertEquals(1, observingBean.getBarObserverCalled());

        observingBean.reset();
        bean.bar(false);
        assertEquals(0, observingBean.getFooObserverCalled());
        assertEquals(0, observingBean.getFooBarObserverCalled());
        assertEquals(0, observingBean.getBarObserverCalled());

        observingBean.reset();
        try {
            bean.bar(true);
        } catch (IllegalStateException e) {
            // expected
        }
        assertEquals(0, observingBean.getFooObserverCalled());
        assertEquals(0, observingBean.getFooBarObserverCalled());
        assertEquals(0, observingBean.getBarObserverCalled());
    }

    @Test
    public void testEventParameters() {
    	Events events = Events.instance();
        String string = "Jozef";
        Integer integer = 13;
        Collection<?> collection = new LinkedList<Object>();

        observingBean.reset();
        events.raiseEvent("parameters", string, integer, collection);
        assertEquals(string, observingBean.getP1());
        assertEquals(integer, observingBean.getP2());
        assertEquals(collection, observingBean.getP3());
    }

    @Test
    @Ignore // CDI-124
    public void testReception() {
    	Events events = Events.instance();
        events.raiseEvent("ignoredEvent");
        assertFalse(NonInitializedObservingBean.isObserverCalled());
    }
    
    @Test
    public void testAsynchronousEvent()
    {
    	Events events = Events.instance();
        observingBean.reset();
        assertEquals(0, observingBean.getAsyncObserverCalled());
        events.raiseAsynchronousEvent("asynchronous");
        assertEquals(0, observingBean.getAsyncObserverCalled());
        try {
            Thread.sleep(2000); // this is not bulletproof but should give us an idea
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertEquals(1, observingBean.getAsyncObserverCalled());
    }
    
    @Test
    public void testTimedEvent()
    {
    	Events events = Events.instance();
        observingBean.reset();
        assertEquals(0, observingBean.getTimedEventObserverCalled());
        events.raiseTimedEvent("timed", new Schedule(1000l), "bang!");
        assertEquals(0, observingBean.getTimedEventObserverCalled());
        try {
            Thread.sleep(2000); // this is not bulletproof but should give us an idea
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertEquals(1, observingBean.getTimedEventObserverCalled());
        assertEquals("bang!", observingBean.getTimedEventPayload());
    }
    
    @Test
    public void testObserverResolution(BeanManager manager)
    {
        EventPayload payload = new EventPayload("");
        assertEquals(2, manager.resolveObserverMethods(payload, new EventQualifier.EventQualifierLiteral("foo", TransactionPhase.IN_PROGRESS)).size());
        assertEquals(1, manager.resolveObserverMethods(payload, new EventQualifier.EventQualifierLiteral("ignoredEvent", TransactionPhase.IN_PROGRESS)).size());
        
        assertEquals(2, manager.resolveObserverMethods(payload, new EventQualifier.EventQualifierLiteral("foo", TransactionPhase.AFTER_COMPLETION)).size());
        assertEquals(1, manager.resolveObserverMethods(payload, new EventQualifier.EventQualifierLiteral("ignoredEvent", TransactionPhase.AFTER_COMPLETION)).size());
        
        assertEquals(2, manager.resolveObserverMethods(payload, new EventQualifier.EventQualifierLiteral("foo", TransactionPhase.AFTER_SUCCESS)).size());
        assertEquals(1, manager.resolveObserverMethods(payload, new EventQualifier.EventQualifierLiteral("ignoredEvent", TransactionPhase.AFTER_SUCCESS)).size());
    }
}
