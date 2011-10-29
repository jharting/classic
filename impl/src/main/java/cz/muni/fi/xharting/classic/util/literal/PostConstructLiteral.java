package cz.muni.fi.xharting.classic.util.literal;

import javax.annotation.PostConstruct;
import javax.enterprise.util.AnnotationLiteral;

@SuppressWarnings("all")
public class PostConstructLiteral extends AnnotationLiteral<PostConstruct> implements PostConstruct {

    private static final long serialVersionUID = 8691378412153203813L;
    public static final PostConstructLiteral INSTANCE = new PostConstructLiteral();
}
