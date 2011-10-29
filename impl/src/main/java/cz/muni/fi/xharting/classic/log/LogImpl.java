package cz.muni.fi.xharting.classic.log;

import java.io.Serializable;

import org.jboss.seam.log.Log;
import org.jboss.solder.el.Expressions;
import org.jboss.solder.logging.Logger;
import static org.jboss.solder.logging.Logger.Level;

/**
 * Log implementation that delegates calls to Solder logging
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 */
public class LogImpl implements Log, Serializable {

    private static final long serialVersionUID = -5096988340912397556L;
    private Logger delegate;
    private Expressions expressions;

    public LogImpl(Logger delegate, Expressions expressions) {
        this.delegate = delegate;
        this.expressions = expressions;
    }

    public boolean isDebugEnabled() {
        return delegate.isEnabled(Level.DEBUG);
    }

    public boolean isErrorEnabled() {
        return delegate.isEnabled(Level.ERROR);
    }

    public boolean isFatalEnabled() {
        return delegate.isEnabled(Level.FATAL);
    }

    public boolean isInfoEnabled() {
        return delegate.isEnabled(Level.INFO);
    }

    public boolean isTraceEnabled() {
        return delegate.isEnabled(Level.TRACE);
    }

    public boolean isWarnEnabled() {
        return delegate.isEnabled(Level.WARN);
    }

    @Override
    public void trace(Object object, Object... params) {
        delegate.tracev(interpolate(object), params);
    }

    @Override
    public void trace(Object object, Throwable t, Object... params) {
        delegate.tracev(t, interpolate(object), params);
    }

    @Override
    public void debug(Object object, Object... params) {
        delegate.debugv(interpolate(object), params);
    }

    @Override
    public void debug(Object object, Throwable t, Object... params) {
        delegate.debugv(t, interpolate(object), params);
    }

    @Override
    public void info(Object object, Object... params) {
        delegate.infov(interpolate(object), params);
    }

    @Override
    public void info(Object object, Throwable t, Object... params) {
        delegate.infov(t, interpolate(object), params);
    }

    @Override
    public void warn(Object object, Object... params) {
        delegate.warnv(interpolate(object), params);
    }

    @Override
    public void warn(Object object, Throwable t, Object... params) {
        delegate.warnv(t, interpolate(object), params);
    }

    @Override
    public void error(Object object, Object... params) {
        delegate.errorv(interpolate(object), params);
    }

    @Override
    public void error(Object object, Throwable t, Object... params) {
        delegate.errorv(t, interpolate(object), params);
    }

    @Override
    public void fatal(Object object, Object... params) {
        delegate.fatalv(interpolate(object), params);
    }

    @Override
    public void fatal(Object object, Throwable t, Object... params) {
        delegate.fatalv(t, interpolate(object), params);
    }

    /**
     * Converts Object to String. If the parameter is an instance of String containing the '#' character, EL expressions are
     * evaluated. Furthermore, parameter placeholders written in the old format (#0) are replaced by placeholders used by
     * MessageFormat ({0}).
     */
    protected String interpolate(Object object) {
        if (object instanceof String) {
            String message = (String) object;
            if (message.contains("#")) {
                message = expressions.evaluateValueExpression(message);
            }
            if (message.contains("#")) {
                // convert the format used in Seam 2 (#0) to the format used by MessageFormat
                message = message.replaceAll("#(\\d+)", "\\{$1\\}");
            }
            return message;
        }
        return object.toString();
    }
}
