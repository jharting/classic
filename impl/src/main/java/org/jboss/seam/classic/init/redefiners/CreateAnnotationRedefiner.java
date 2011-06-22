package org.jboss.seam.classic.init.redefiners;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.classic.util.literals.PostConstructLiteral;
import org.jboss.seam.solder.reflection.annotated.AnnotationBuilder;
import org.jboss.seam.solder.reflection.annotated.AnnotationRedefiner;
import org.jboss.seam.solder.reflection.annotated.RedefinitionContext;

public class CreateAnnotationRedefiner implements AnnotationRedefiner<Create> {

    @Override
    public void redefine(RedefinitionContext<Create> ctx) {
        AnnotationBuilder builder = ctx.getAnnotationBuilder();
        builder.add(PostConstructLiteral.INSTANCE);
    }

}
