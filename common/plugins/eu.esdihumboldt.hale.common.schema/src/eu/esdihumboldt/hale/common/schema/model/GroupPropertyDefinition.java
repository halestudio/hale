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

package eu.esdihumboldt.hale.common.schema.model;

/**
 * Defines a group property
 * 
 * @author Simon Templer
 */
public interface GroupPropertyDefinition extends DefinitionGroup,
		ChildDefinition<GroupPropertyConstraint> {

	// concrete typed interface

	/**
	 * States if the group may be flattened, i.e. that the group's children may
	 * be added to the group's parent instead of itself.<br>
	 * This can be reasonable for groups that are only created because at
	 * creation time the children are not yet determined.
	 * 
	 * @return if the group may be replaced by its children
	 */
	public boolean allowFlatten();

}
