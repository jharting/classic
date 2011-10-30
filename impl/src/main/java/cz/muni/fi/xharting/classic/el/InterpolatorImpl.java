package cz.muni.fi.xharting.classic.el;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.core.Interpolator;
import org.jboss.solder.el.Expressions;

/**
 * Interpolates EL expressions in Strings
 * 
 * @author Gavin King
 * @author Jozef Hartinger
 */
@ApplicationScoped
@Named("org.jboss.seam.core.interpolator")
public class InterpolatorImpl extends Interpolator {

    @Inject
    private Expressions expressions;

    /**
     * Replace all EL expressions in the form #{...} with their evaluated values.
     * 
     * @param string a template
     * @return the interpolated string
     */
    public String interpolate(String string, Object... params) {
        if (string.contains("#")) {
            String result = string;
            if (params.length != 0) {
                throw new UnsupportedOperationException("Parameters not supported. Sorry"); // TODO
            }
            result = expressions.evaluateValueExpression(string, String.class);
            return result;
        }
        return string;
    }
}
