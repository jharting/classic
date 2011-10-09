package org.jboss.seam.classic.test.config;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("observingBean")
@Scope(ScopeType.APPLICATION)
public class ObservingBean {

    private int alpha = 0;
    private int bravo = 0;

    public void alpha() {
        alpha++;
    }
    
    public void bravo()
    {
        bravo++;
    }

    public int getAlpha() {
        return alpha;
    }

    public int getBravo() {
        return bravo;
    }

}
