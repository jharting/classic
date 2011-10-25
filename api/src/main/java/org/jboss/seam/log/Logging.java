package org.jboss.seam.log;

import org.jboss.seam.util.StaticLookup;

/**
 * Factory for Seam Logs.
 * 
 * @author Gavin King
 * @author Jozef Hartinger
 * 
 */
public abstract class Logging {

    public static Log getLog(String category) {
        return getInstance().internalGetLog(category);
    }

    @SuppressWarnings("rawtypes")
    public static Log getLog(Class clazz) {
        return getInstance().internalGetLog(clazz);
    }

    protected static Logging getInstance() {
        return StaticLookup.lookupBean(Logging.class);
    }

    public static LogProvider getLogProvider(String category, boolean wrapped) {
        throw new UnsupportedOperationException("Pluggable log providers not supported.");
    }

    @SuppressWarnings("rawtypes")
    public static LogProvider getLogProvider(Class clazz) {
        throw new UnsupportedOperationException("Pluggable log providers not supported.");
    }

    protected abstract Log internalGetLog(Class<?> clazz);

    protected abstract Log internalGetLog(String category);
}
