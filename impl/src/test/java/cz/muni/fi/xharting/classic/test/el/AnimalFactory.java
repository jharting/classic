package cz.muni.fi.xharting.classic.test.el;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("animalFactory")
@Scope(ScopeType.APPLICATION)
public class AnimalFactory {

    @Factory("quick")
    public Animal getQuickAnimal() {
        return new Animal("fox");
    }

    @Factory("lazy")
    public Animal getLazyAnimal() {
        return new Animal("dog");
    }

}
