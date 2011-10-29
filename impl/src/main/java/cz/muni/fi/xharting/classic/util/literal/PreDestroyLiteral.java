package cz.muni.fi.xharting.classic.util.literal;

import javax.annotation.PreDestroy;
import javax.enterprise.util.AnnotationLiteral;

@SuppressWarnings("all")
public class PreDestroyLiteral extends AnnotationLiteral<PreDestroy> implements PreDestroy {

    private static final long serialVersionUID = -6364086391667270584L;
    public static final PreDestroyLiteral INSTANCE = new PreDestroyLiteral();
}
