package cz.muni.fi.xharting.classic.test.util.reference;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Alpha {

    @Inject
    @SuppressWarnings("unused")
    private Bravo bean;
}
