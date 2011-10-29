package cz.muni.fi.xharting.classic.bootstrap.redefiners;

import org.jboss.seam.annotations.Create;
import org.jboss.solder.reflection.annotated.AnnotationBuilder;
import org.jboss.solder.reflection.annotated.AnnotationRedefiner;
import org.jboss.solder.reflection.annotated.RedefinitionContext;

import cz.muni.fi.xharting.classic.util.literal.PostConstructLiteral;

public class CreateAnnotationRedefiner implements AnnotationRedefiner<Create> {

    @Override
    public void redefine(RedefinitionContext<Create> ctx) {
        AnnotationBuilder builder = ctx.getAnnotationBuilder();
        builder.add(PostConstructLiteral.INSTANCE);
    }

}
