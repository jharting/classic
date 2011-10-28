package org.jboss.seam.classic.init;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;

import org.jboss.seam.classic.init.scan.Scanner;

/**
 * The {@link CoreExtension} uses this event during bootstrap to notify other extensions that scanning finished.
 * Extensions can register qualifiers, scopes, etc. similarly to @{link BeforeBeanDiscovery}.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 */
public class ScanningCompleteEvent {

    private final Scanner scanner;
    private final BeforeBeanDiscovery bbd;

    protected ScanningCompleteEvent(Scanner scanner, BeforeBeanDiscovery bbd) {
        this.scanner = scanner;
        this.bbd = bbd;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void addQualifier(Class<? extends Annotation> qualifier) {
        bbd.addQualifier(qualifier);
    }

    public void addScope(Class<? extends Annotation> scopeType, boolean normal, boolean passivating) {
        bbd.addScope(scopeType, normal, passivating);
    }

    public void addStereotype(Class<? extends Annotation> stereotype, Annotation... stereotypeDef) {
        bbd.addStereotype(stereotype, stereotypeDef);
    }

    public void addInterceptorBinding(Class<? extends Annotation> bindingType, Annotation... bindingTypeDef) {
        bbd.addInterceptorBinding(bindingType, bindingTypeDef);
    }

    public void addAnnotatedType(AnnotatedType<?> type) {
        bbd.addAnnotatedType(type);
    }
}
