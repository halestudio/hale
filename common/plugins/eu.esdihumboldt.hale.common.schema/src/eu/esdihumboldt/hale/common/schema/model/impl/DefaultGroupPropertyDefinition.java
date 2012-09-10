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

import java.util.Collection;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

/**
 * Default {@link GroupPropertyDefinition} implementation
 * 
 * @author Simon Templer
 */
public class DefaultGroupPropertyDefinition extends
		AbstractChildDefinition<GroupPropertyConstraint> implements GroupPropertyDefinition {

	private final DefinitionGroup children = new DefaultGroup(true);

	private final boolean allowFlatten;

	/**
	 * Create a new group property
	 * 
	 * @param name the group name
	 * @param parentGroup the parent group
	 * @param allowFlatten if the group may be replaced by its children
	 * 
	 * @see GroupPropertyDefinition#allowFlatten()
	 */
	public DefaultGroupPropertyDefinition(QName name, DefinitionGroup parentGroup,
			boolean allowFlatten) {
		super(name, parentGroup);

		this.allowFlatten = allowFlatten;
	}

	/**
	 * @see GroupPropertyDefinition#allowFlatten()
	 */
	@Override
	public boolean allowFlatten() {
		return allowFlatten || getDeclaredChildren().isEmpty(); // always allow
																// flattening
																// (removing) an
																// empty group
	}

	/**
	 * @see DefinitionGroup#getDeclaredChildren()
	 */
	@Override
	public Collection<? extends ChildDefinition<?>> getDeclaredChildren() {
		return children.getDeclaredChildren();
	}

	/**
	 * @see DefinitionGroup#getChild(QName)
	 */
	@Override
	public ChildDefinition<?> getChild(QName name) {
		return children.getChild(name);
	}

	/**
	 * @see DefinitionGroup#addChild(ChildDefinition)
	 */
	@Override
	public void addChild(ChildDefinition<?> child) {
		children.addChild(child);
	}

	/**
	 * @see ChildDefinition#asProperty()
	 */
	@Override
	public PropertyDefinition asProperty() {
		return null;
	}

	/**
	 * @see ChildDefinition#asGroup()
	 */
	@Override
	public GroupPropertyDefinition asGroup() {
		return this;
	}

	/**
	 * @see AbstractDefinition#toString()
	 */
	@Override
	public String toString() {
		return "[group] " + super.toString();
	}

}
