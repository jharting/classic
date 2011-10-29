package cz.muni.fi.xharting.classic.util.literal;

import javax.enterprise.util.AnnotationLiteral;

import org.jboss.solder.servlet.http.RequestParam;

@SuppressWarnings("all")
public class RequestParamLiteral extends AnnotationLiteral<RequestParam> implements RequestParam {

    private static final long serialVersionUID = 1235118464286179120L;
    private String value;
    
    public RequestParamLiteral(String value) {
        this.value = value;
    }
    
    @Override
    public String value() {
        return value;
    }
}
