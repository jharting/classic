package cz.muni.fi.xharting.classic.test.log;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

@SuppressWarnings("serial")
@Name("loggingBean")
@Scope(ScopeType.SESSION)
public class LoggingBean implements Serializable {

    @Logger("explicit.category")
    private Log log1;
    @Logger
    private Log log2;
    
    private Log log3 = Logging.getLog("foo.bar");
    
    public void logSomething()
    {
        log1.info("Creating new order for user: #{user.username} quantity: #0", 13);
        log2.info("Creating new order for user: #{user.username} quantity: #0", 13);
        log3.info("Creating new order for user: #{user.username} quantity: #0", 13);
    }
}
