package cz.muni.fi.xharting.classic.bootstrap.redefiners;

import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.solder.literal.InjectLiteral;
import org.jboss.solder.reflection.annotated.AnnotationBuilder;
import org.jboss.solder.reflection.annotated.AnnotationRedefiner;
import org.jboss.solder.reflection.annotated.RedefinitionContext;

import cz.muni.fi.xharting.classic.util.literal.RequestParamLiteral;

public class RequestParameterRedefiner implements AnnotationRedefiner<RequestParameter> {

    @Override
    public void redefine(RedefinitionContext<RequestParameter> ctx) {
        AnnotationBuilder builder = ctx.getAnnotationBuilder();
        RequestParameter requestParameter = builder.getAnnotation(RequestParameter.class);
        RequestParamLiteral replacement = new RequestParamLiteral(requestParameter.value());
        
        builder.remove(RequestParameter.class);
        builder.add(replacement);
        builder.add(InjectLiteral.INSTANCE);
    }

}
