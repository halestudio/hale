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

	private final DefinitionGroup children;

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
		this.children = new DefaultGroup(getIdentifier() + "/children", true);
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
