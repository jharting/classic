package org.jboss.seam.classic.init.redefiners;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.classic.util.literals.PreDestroyLiteral;
import org.jboss.solder.reflection.annotated.AnnotationBuilder;
import org.jboss.solder.reflection.annotated.AnnotationRedefiner;
import org.jboss.solder.reflection.annotated.RedefinitionContext;

public class DestroyAnnotationRedefiner implements AnnotationRedefiner<Destroy> {

    @Override
    public void redefine(RedefinitionContext<Destroy> ctx) {
        AnnotationBuilder builder = ctx.getAnnotationBuilder();
        builder.add(PreDestroyLiteral.INSTANCE);

    }
}
