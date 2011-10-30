package cz.muni.fi.xharting.classic.test.persistence.entity;

import java.io.Serializable;

import javax.persistence.Entity;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Scope;

@SuppressWarnings("serial")
@Name("entity")
@Role(name = "entity2", scope = ScopeType.APPLICATION)
@Scope(ScopeType.SESSION)
@Entity
public class SerializableEntity implements Serializable {
}
