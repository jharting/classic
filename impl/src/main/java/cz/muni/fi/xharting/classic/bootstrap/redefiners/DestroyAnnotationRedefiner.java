package cz.muni.fi.xharting.classic.bootstrap.redefiners;

import org.jboss.seam.annotations.Destroy;
import org.jboss.solder.reflection.annotated.AnnotationBuilder;
import org.jboss.solder.reflection.annotated.AnnotationRedefiner;
import org.jboss.solder.reflection.annotated.RedefinitionContext;

import cz.muni.fi.xharting.classic.util.literal.PreDestroyLiteral;

public class DestroyAnnotationRedefiner implements AnnotationRedefiner<Destroy> {

    @Override
    public void redefine(RedefinitionContext<Destroy> ctx) {
        AnnotationBuilder builder = ctx.getAnnotationBuilder();
        builder.add(PreDestroyLiteral.INSTANCE);

    }
}
