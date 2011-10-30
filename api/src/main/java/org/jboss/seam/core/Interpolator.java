package org.jboss.seam.core;

import org.jboss.seam.util.StaticLookup;

/**
 * Interpolates EL expressions in Strings
 * 
 * @author Gavin King
 */
public abstract class Interpolator {

    public static Interpolator instance() {
        return StaticLookup.lookupBean(Interpolator.class);
    }

    /**
     * Replace all EL expressions in the form #{...} with their evaluated values.
     * 
     * @param string a template
     * @return the interpolated string
     */
    public abstract String interpolate(String string, Object... params);
}