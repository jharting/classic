package cz.muni.fi.xharting.classic.test.injection;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("hotelSubclass")
@Scope(ScopeType.APPLICATION)
public class HotelSubclass extends Hotel {

    @Override
    public void checkInjection() {
        super.checkInjection();
    }
}
