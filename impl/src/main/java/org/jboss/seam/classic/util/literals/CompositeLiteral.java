package org.jboss.seam.classic.util.literals;

import javax.enterprise.util.AnnotationLiteral;

import org.jboss.solder.el.Composite;

@SuppressWarnings("all")
public class CompositeLiteral extends AnnotationLiteral<Composite> implements Composite {

    public static final CompositeLiteral INSTANCE = new CompositeLiteral();

}
