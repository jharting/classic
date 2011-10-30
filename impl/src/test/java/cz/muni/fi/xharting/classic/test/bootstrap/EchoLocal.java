package cz.muni.fi.xharting.classic.test.bootstrap;

import javax.ejb.Local;

@Local
public interface EchoLocal {

    public void ping();
    
}
