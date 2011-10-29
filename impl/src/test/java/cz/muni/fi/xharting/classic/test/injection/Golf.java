package cz.muni.fi.xharting.classic.test.injection;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("golf")
public class Golf extends AbstractComponent{

    @SuppressWarnings("unused")
    @In
    private Delta delta;
}
