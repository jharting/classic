package cz.muni.fi.xharting.classic.test.util.reference;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import cz.muni.fi.xharting.classic.util.reference.DirectReference;

@ApplicationScoped
@DirectReference
public class Foo {

    @Produces
    @ApplicationScoped
    @DirectReference
    @SuppressWarnings("unused")
    private String bar = "bar";
}
