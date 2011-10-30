package cz.muni.fi.xharting.classic.test.persistence.entity;

import javax.persistence.Entity;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("entity3")
@Scope(ScopeType.EVENT)
@Entity
public class NonSerializableEntity {

}
