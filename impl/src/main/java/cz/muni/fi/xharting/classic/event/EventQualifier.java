package cz.muni.fi.xharting.classic.event;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import javax.enterprise.event.TransactionPhase;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Target;

@Target({FIELD, PARAMETER})
@Qualifier
@Retention(RUNTIME)
public @interface EventQualifier {

    String name();
    
    TransactionPhase transactionPhase();
    
    @SuppressWarnings("all")
    public static class EventQualifierLiteral extends AnnotationLiteral<EventQualifier> implements EventQualifier {

        private static final long serialVersionUID = -3511232440719418509L;
        private String name;
        private TransactionPhase transactionPhase;
        
        public EventQualifierLiteral(String name, TransactionPhase transactionPhase) {
            this.name = name;
            this.transactionPhase = transactionPhase;
        }

        public String name() {
            return name;
        }

        public TransactionPhase transactionPhase() {
            return transactionPhase;
        }

    }
}
