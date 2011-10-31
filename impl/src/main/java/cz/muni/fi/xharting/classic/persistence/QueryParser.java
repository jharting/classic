package cz.muni.fi.xharting.classic.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Copied from Seam 2.
 * 
 * @author Gavin King
 * @author Jozef Hartinger
 * 
 */
public class QueryParser {
    private List<String> parameterValueExpressions = new ArrayList<String>();
    private StringBuilder ejbqlBuilder;

    public static String getParameterName(int loc) {
        return "el" + (loc + 1);
    }

    public String getEjbql() {
        return ejbqlBuilder.toString();
    }

    public List<String> getParameterValueExpressions() {
        return parameterValueExpressions;
    }

    public QueryParser(String ejbql) {
        this(ejbql, 0);
    }

    public QueryParser(String ejbql, int startingParameterNumber) {
        StringTokenizer tokens = new StringTokenizer(ejbql, "#}", true);
        ejbqlBuilder = new StringBuilder(ejbql.length());
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if ("#".equals(token) && tokens.hasMoreTokens()) {
                String expressionToken = tokens.nextToken();

                if (!expressionToken.startsWith("{") || !tokens.hasMoreTokens()) {
                    ejbqlBuilder.append(token).append(expressionToken);
                } else {
                    String expression = token + expressionToken + tokens.nextToken();
                    ejbqlBuilder.append(':').append(
                            getParameterName(startingParameterNumber + parameterValueExpressions.size()));
                    parameterValueExpressions.add(expression);
                }
            } else {
                ejbqlBuilder.append(token);
            }
        }
    }

}
