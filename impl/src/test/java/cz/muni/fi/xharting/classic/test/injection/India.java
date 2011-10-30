package cz.muni.fi.xharting.classic.test.injection;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Stateless
@Name("india")
@Scope(ScopeType.STATELESS)
@AutoCreate
public class India implements IndiaLocal {

    @In
    private Echo echo;

    @Override
    public boolean injected() {
        return echo != null;
    }

}
