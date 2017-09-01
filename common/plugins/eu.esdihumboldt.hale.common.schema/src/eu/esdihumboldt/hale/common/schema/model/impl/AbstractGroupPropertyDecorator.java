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
import java.util.Collection;

import javax.xml.namespace.QName;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Abstract decorator for {@link GroupPropertyDefinition}s
 * 
 * @author Simon Templer
 */
@Immutable
public abstract class AbstractGroupPropertyDecorator implements GroupPropertyDefinition {

	/**
	 * The internal property definition
	 */
	protected final GroupPropertyDefinition propertyGroup;

	/**
	 * Create a property group definition decorator
	 * 
	 * @param propertyGroup the internal property group definition
	 */
	public AbstractGroupPropertyDecorator(GroupPropertyDefinition propertyGroup) {
		super();
		this.propertyGroup = propertyGroup;
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(Definition<?> o) {
		return propertyGroup.compareTo(o);
	}

	/**
	 * @see Locatable#getLocation()
	 */
	@Override
	public URI getLocation() {
		return propertyGroup.getLocation();
	}

	/**
	 * @see Definition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return propertyGroup.getIdentifier();
	}

	/**
	 * @see Definition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return propertyGroup.getDisplayName();
	}

	/**
	 * @see Definition#getName()
	 */
	@Override
	public QName getName() {
		return propertyGroup.getName();
	}

	/**
	 * @see Definition#getDescription()
	 */
	@Override
	public String getDescription() {
		return propertyGroup.getDescription();
	}

	/**
	 * @see PropertyDefinition#getDeclaringGroup()
	 */
	@Override
	public DefinitionGroup getDeclaringGroup() {
		return propertyGroup.getDeclaringGroup();
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
		return propertyGroup.getParentType();
	}

	/**
	 * @see ChildDefinition#asProperty()
	 */
	@Override
	public PropertyDefinition asProperty() {
		return propertyGroup.asProperty();
	}

	/**
	 * @see ChildDefinition#asGroup()
	 */
	@Override
	public GroupPropertyDefinition asGroup() {
		return this;
	}

	/**
	 * @see DefinitionGroup#getDeclaredChildren()
	 */
	@Override
	public Collection<? extends ChildDefinition<?>> getDeclaredChildren() {
		return propertyGroup.getDeclaredChildren();
	}

	/**
	 * @see DefinitionGroup#getChild(QName)
	 */
	@Override
	public ChildDefinition<?> getChild(QName name) {
		return propertyGroup.getChild(name);
	}

	/**
	 * @see DefinitionGroup#addChild(ChildDefinition)
	 */
	@Override
	public void addChild(ChildDefinition<?> child) {
		propertyGroup.addChild(child);
	}

	/**
	 * @see Definition#getConstraint(Class)
	 */
	@Override
	public <T extends GroupPropertyConstraint> T getConstraint(Class<T> constraintType) {
		return propertyGroup.getConstraint(constraintType);
	}

	@Override
	public Iterable<GroupPropertyConstraint> getExplicitConstraints() {
		return propertyGroup.getExplicitConstraints();
	}

	/**
	 * @see GroupPropertyDefinition#allowFlatten()
	 */
	@Override
	public boolean allowFlatten() {
		return propertyGroup.allowFlatten();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return propertyGroup.toString();
	}

	/**
	 * @return internal property group definition
	 */
	public GroupPropertyDefinition getDecorated() {
		return propertyGroup;
	}

}
