package cz.muni.fi.xharting.classic.test.config.beans;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@SuppressWarnings("serial")
@Name("falcon")
@Scope(ScopeType.EVENT)
@Install(false)
@AutoCreate
public class Falcon implements Serializable {

}
