package cz.muni.fi.xharting.classic.bootstrap.redefiners;

import javax.annotation.PostConstruct;

import org.jboss.seam.annotations.Create;
import org.jboss.solder.reflection.annotated.AnnotationBuilder;
import org.jboss.solder.reflection.annotated.AnnotationRedefiner;
import org.jboss.solder.reflection.annotated.RedefinitionContext;

import cz.muni.fi.xharting.classic.util.literal.PostConstructLiteral;

/**
 * Provides trivial syntactic transformation of {@link Create} to {@link PostConstruct}.
 * 
 * @author Jozef Hartinger
 * 
 */
public class CreateAnnotationRedefiner implements AnnotationRedefiner<Create> {

    @Override
    public void redefine(RedefinitionContext<Create> ctx) {
        AnnotationBuilder builder = ctx.getAnnotationBuilder();
        builder.add(PostConstructLiteral.INSTANCE);
    }

}
