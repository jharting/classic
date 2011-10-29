package cz.muni.fi.xharting.classic.util.literal;

import javax.enterprise.util.AnnotationLiteral;

import org.jboss.seam.annotations.Synchronized;

@SuppressWarnings("all")
public class SynchronizedLiteral extends AnnotationLiteral<Synchronized> implements Synchronized {

    private static final long serialVersionUID = -1504929957396761801L;

    public static final SynchronizedLiteral DEFAULT_INSTANCE = new SynchronizedLiteral();

    private final long timeout;

    private SynchronizedLiteral() {
        this.timeout = Synchronized.DEFAULT_TIMEOUT;
    }

    public SynchronizedLiteral(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public long timeout() {
        return timeout;
    }
}
