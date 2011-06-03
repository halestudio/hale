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

package eu.esdihumboldt.hale.schema.model.impl;

import java.net.URI;
import java.util.Collection;

import javax.xml.namespace.QName;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.schema.model.Definition;
import eu.esdihumboldt.hale.schema.model.Group;
import eu.esdihumboldt.hale.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;

/**
 * Abstract decorator for {@link GroupPropertyDefinition}s
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
	public Group getDeclaringGroup() {
		return propertyGroup.getDeclaringGroup();
	}

	/**
	 * @see PropertyDefinition#getParentType()
	 */
	@Override
	public TypeDefinition getParentType() {
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
		return propertyGroup.asGroup();
	}

	/**
	 * @see Group#getDeclaredChildren()
	 */
	@Override
	public Collection<? extends ChildDefinition<?>> getDeclaredChildren() {
		return propertyGroup.getDeclaredChildren();
	}

	/**
	 * @see Group#getChild(QName)
	 */
	@Override
	public ChildDefinition<?> getChild(QName name) {
		return propertyGroup.getChild(name);
	}

	/**
	 * @see Group#addChild(ChildDefinition)
	 */
	@Override
	public void addChild(ChildDefinition<?> child) {
		propertyGroup.addChild(child);
	}

	/**
	 * @see Definition#getConstraint(Class)
	 */
	@Override
	public <T extends GroupPropertyConstraint> T getConstraint(
			Class<T> constraintType) {
		return propertyGroup.getConstraint(constraintType);
	}

}
