/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.schema.model.impl;

import java.net.URI;

import javax.xml.namespace.QName;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Abstract decorator for {@link PropertyDefinition}s
 * 
 * @author Simon Templer
 */
@Immutable
public abstract class AbstractPropertyDecorator implements PropertyDefinition {

	/**
	 * The internal property definition
	 */
	protected final PropertyDefinition property;

	/**
	 * Create a property definition decorator
	 * 
	 * @param property the internal property definition
	 */
	public AbstractPropertyDecorator(PropertyDefinition property) {
		super();
		this.property = property;
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(Definition<?> o) {
		return property.compareTo(o);
	}

	/**
	 * @see Locatable#getLocation()
	 */
	@Override
	public URI getLocation() {
		return property.getLocation();
	}

	/**
	 * @see Definition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return property.getIdentifier();
	}

	/**
	 * @see Definition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return property.getDisplayName();
	}

	/**
	 * @see Definition#getName()
	 */
	@Override
	public QName getName() {
		return property.getName();
	}

	/**
	 * @see Definition#getDescription()
	 */
	@Override
	public String getDescription() {
		return property.getDescription();
	}

	/**
	 * @see Definition#getConstraint(Class)
	 */
	@Override
	public <T extends PropertyConstraint> T getConstraint(Class<T> constraintType) {
		return property.getConstraint(constraintType);
	}

	@Override
	public Iterable<PropertyConstraint> getExplicitConstraints() {
		return property.getExplicitConstraints();
	}

	/**
	 * @see PropertyDefinition#getDeclaringGroup()
	 */
	@Override
	public DefinitionGroup getDeclaringGroup() {
		return property.getDeclaringGroup();
	}

	/**
	 * @see PropertyDefinition#getParentType()
	 */
	@Override
	public TypeDefinition getParentType() {
		/*
		 * For cases in which getDeclaringGroup() is overridden, but not
		 * getParentType(), return result according to getDeclaringGroup()
		 */
		DefinitionGroup parent = getDeclaringGroup();

		if (parent instanceof TypeDefinition) {
			return (TypeDefinition) parent;
		}
		else if (parent instanceof ChildDefinition<?>) {
			return ((ChildDefinition<?>) parent).getParentType();
		}

		// fall back to decoratee
		return property.getParentType();
	}

	/**
	 * @see PropertyDefinition#getPropertyType()
	 */
	@Override
	public TypeDefinition getPropertyType() {
		return property.getPropertyType();
	}

	/**
	 * @see ChildDefinition#asProperty()
	 */
	@Override
	public PropertyDefinition asProperty() {
		return this;
	}

	/**
	 * @see ChildDefinition#asGroup()
	 */
	@Override
	public GroupPropertyDefinition asGroup() {
		return property.asGroup();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return property.toString();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Definition)
			return DefinitionUtil.equal(this, (Definition<?>) obj);
		else
			return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	/**
	 * @return the original property decorated by this decorator
	 */
	public PropertyDefinition getDecoratedProperty() {
		return property;
	}
}
