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

package eu.esdihumboldt.hale.common.instance.model;

import java.util.Collection;

/**
 * FamilyInstance is an Instance with functionality to add child instance links.
 * 
 * @author Kai Schwierczek
 */
public interface FamilyInstance extends IdentifiableInstance {

	/**
	 * Returns the child instances.
	 * 
	 * @return the child instances
	 */
	public Collection<FamilyInstance> getChildren();

	/**
	 * Adds the given instance as child to this instance.
	 * 
	 * @param child the child instance to add
	 */
	public void addChild(FamilyInstance child);
}
