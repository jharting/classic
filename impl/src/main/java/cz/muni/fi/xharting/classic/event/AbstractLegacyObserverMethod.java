package cz.muni.fi.xharting.classic.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ObserverMethod;

public abstract class AbstractLegacyObserverMethod implements ObserverMethod<EventPayload> {

    private EventQualifier qualifier;
    private Reception reception;
    private BeanManager manager;

    public AbstractLegacyObserverMethod(String type, TransactionPhase transactionPhase, boolean autoCreate, BeanManager manager) {
        qualifier = new EventQualifier.EventQualifierLiteral(type, transactionPhase);
        this.manager = manager;
        if (autoCreate) {
            reception = Reception.ALWAYS;
        } else {
            reception = Reception.IF_EXISTS;
        }
    }

    protected EventQualifier getQualifier() {
        return qualifier;
    }

    public BeanManager getManager() {
        return manager;
    }

    public Type getObservedType() {
        return EventPayload.class;
    }

    public Set<Annotation> getObservedQualifiers() {
        return Collections.<Annotation> singleton(qualifier);
    }

    public TransactionPhase getTransactionPhase() {
        return qualifier.transactionPhase();
    }

    public Reception getReception() {
        return reception;
    }
}
