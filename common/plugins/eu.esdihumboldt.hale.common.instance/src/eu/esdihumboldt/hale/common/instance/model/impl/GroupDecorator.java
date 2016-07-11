/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.instance.model.impl;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;

/**
 * Group decorator class.
 * 
 * @author Simon Templer
 */
public class GroupDecorator implements Group {

	private final Group group;

	/**
	 * Constructs the decorator with the given group.
	 * 
	 * @param group the group to decorate
	 */
	public GroupDecorator(Group group) {
		this.group = group;
	}

	/**
	 * Returns the original group.
	 * 
	 * @return the original group
	 */
	public Group getOriginalGroup() {
		return group;
	}

	@Override
	public Object[] getProperty(QName propertyName) {
		return group.getProperty(propertyName);
	}

	@Override
	public Iterable<QName> getPropertyNames() {
		return group.getPropertyNames();
	}

	@Override
	public DefinitionGroup getDefinition() {
		return group.getDefinition();
	}

}
