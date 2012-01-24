/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.schema.model.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.constraint.ConstraintUtil;
import eu.esdihumboldt.hale.common.schema.model.constraint.DisplayName;

/**
 * Basic definition implementation to be subclassed
 * @param <C> the supported constraint type
 * 
 * @author Simon Templer
 */
public abstract class AbstractDefinition<C> implements Definition<C> {

	/**
	 * The qualified definition name
	 */
	protected final QName name;
	
	/**
	 * The constraints set on the definition
	 */
	private final Map<Class<? extends C>, C> constraints = new HashMap<Class<? extends C>, C>();
	
	/**
	 * The definition description
	 */
	private String description;
	
	/**
	 * The definition location
	 */
	private URI location;

	/**
	 * Creates a new definition with the given name. Description and location
	 * are not set.
	 * 
	 * @param name the qualified definition name
	 */
	public AbstractDefinition(QName name) {
		super();
		
		this.name = name;
	}
	
	/**
	 * @see Definition#getConstraint(Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends C> T getConstraint(Class<T> constraintType) {
		synchronized (constraints) {
			C constraint = constraints.get(constraintType);
			if (constraint != null) {
				return (T) constraint;
			}
			
			// support for inherited constraints
			T inheritedConstraint = getInheritedConstraint(constraintType);
			if (inheritedConstraint != null) {
				constraints.put(constraintType, inheritedConstraint);
				return inheritedConstraint;
			}
			
			// get default constraint and remember it
			T defConstraint = ConstraintUtil.getDefaultConstraint(constraintType, this);
			constraints.put(constraintType, defConstraint);
			return defConstraint;
		}
	}
	
	/**
	 * Get the inherited constraint of the given constraint type.<br>
	 * <br>
	 * This implementation returns <code>null</code>, as inheritance is not
	 * supported generally for definitions. 
	 * @param constraintType the constraint type
	 * @return the inherited constraint or <code>null</code> if there is none
	 *   or inheritance is not allowed
	 */
	protected <T extends C> T getInheritedConstraint(Class<T> constraintType) {
		return null;
	}
	
	/**
	 * Determines if the constraint with the given type is set explicitly for
	 * the definition.
	 * 
	 * @param constraintType the constraint type
	 * @return if the constraint is set explicitly
	 */
	public boolean hasConstraint(Class<? extends C> constraintType) {
		synchronized (constraints) {
			return constraints.get(constraintType) != null;
		}
	}
	
	/**
	 * Set a constraint on the definition
	 * 
	 * @param constraint the constraint to set
	 */
	@SuppressWarnings("unchecked")
	public void setConstraint(C constraint) {
		synchronized (constraints) {
			// determine constraint type for constraint object
			Class<?> constraintType = ConstraintUtil.getConstraintType(constraint.getClass());
			constraints.put((Class<? extends C>) constraintType, constraint);
		}
	}
	
	/**
	 * Set a constraint on the definition if none of the same type has been set
	 * yet.
	 * @param constraint the constraint to set
	 */
	@SuppressWarnings("unchecked")
	public void setConstraintIfNotSet(C constraint) {
		if (!hasConstraint((Class<? extends C>) ConstraintUtil.getConstraintType(constraint.getClass()))) {
			setConstraint(constraint);
		}
	}

	/**
	 * Set the definition description
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Set the definition location
	 * 
	 * @param location the location to set
	 */
	public void setLocation(URI location) {
		this.location = location;
	}

	/**
	 * @see Locatable#getLocation()
	 */
	@Override
	public URI getLocation() {
		return location;
	}

	/**
	 * Returns the local part of the qualified name. Override to change this
	 * behavior.
	 * @see Definition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		// special treatment for DisplayName constraint (as it can't match the generic type at this point)
		String customName = null;
		try {
			DisplayName dn = (DisplayName) constraints.get(DisplayName.class);
			if (dn != null) {
				customName = dn.getCustomName();
			}
			// creating a default constraint is not done at this point because 
			// the default behavior of DisplayName is to provide no custom name
		} catch (Throwable e) {
			// ignore
		}
		
		if (customName != null) {
			return customName;
		}
		
		return name.getLocalPart();
	}

	/**
	 * @see Definition#getName()
	 */
	@Override
	public QName getName() {
		return name;
	}

	/**
	 * @see Definition#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}
	
	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Two definitions are equal if their name is equal (namespace and local 
	 * part)
	 * 
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractDefinition<?> other = (AbstractDefinition<?>) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.getNamespaceURI().equals(other.name.getNamespaceURI()))
			return false;
		else if (!name.getLocalPart().equals(other.name.getLocalPart()))
			return false;
		return true;
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(Definition<?> other) {
		int result = name.getLocalPart().compareToIgnoreCase(other.getName().getLocalPart());
		if (result == 0) {
			return name.getNamespaceURI().compareToIgnoreCase(other.getName().getNamespaceURI());
		}
		
		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.model.Definition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getIdentifier();
	}
	
}
