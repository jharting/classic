package cz.muni.fi.xharting.classic.test.bootstrap;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.Name;

@Stateless
@Name("echo")
public class Echo implements EchoLocal {

    @Override
    public void ping() {
    }

}
