package cz.muni.fi.xharting.classic.bootstrap.redefiners;

import org.jboss.seam.annotations.Logger;
import org.jboss.solder.literal.InjectLiteral;
import org.jboss.solder.reflection.annotated.AnnotationBuilder;
import org.jboss.solder.reflection.annotated.AnnotationRedefiner;
import org.jboss.solder.reflection.annotated.RedefinitionContext;

public class LoggerRedefiner implements AnnotationRedefiner<Logger> {

    @Override
    public void redefine(RedefinitionContext<Logger> ctx) {
        AnnotationBuilder builder = ctx.getAnnotationBuilder();
        builder.add(InjectLiteral.INSTANCE); // make this a CDI injection point
    }
}
