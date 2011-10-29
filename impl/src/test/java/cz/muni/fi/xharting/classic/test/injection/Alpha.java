package cz.muni.fi.xharting.classic.test.injection;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("alpha")
public class Alpha {

    @In(create = true)
    private Bravo bravo;
    @In(value = "charlie", create = true)
    private Charlie ch;
    @In(required = false)
    private Delta delta; // should be null as create is false by default
    @In
    private Echo echo; // Echo has @AutoCreate
    @In(required = false)
    private Foxtrot foxtrot;
    @In
    private CyclicBean1 cyclicBean1;
    @In(create = true)
    private HotelSubclass hotelSubclass;

    public Bravo getBravo() {
        return bravo;
    }

    public Charlie getCharlie() {
        return ch;
    }

    public Delta getDelta() {
        return delta;
    }

    public Echo getEcho() {
        return echo;
    }

    public Foxtrot getFoxtrot() {
        return foxtrot;
    }

    public CyclicBean1 getCyclicBean1() {
        return cyclicBean1;
    }

    public HotelSubclass getHotelSubclass() {
        return hotelSubclass;
    }

}
