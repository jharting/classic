package org.jboss.seam.classic.init.redefiners;

import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.classic.util.literals.RequestParamLiteral;
import org.jboss.solder.literal.InjectLiteral;
import org.jboss.solder.reflection.annotated.AnnotationBuilder;
import org.jboss.solder.reflection.annotated.AnnotationRedefiner;
import org.jboss.solder.reflection.annotated.RedefinitionContext;

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
