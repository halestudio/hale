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

package eu.esdihumboldt.hale.ui.service.entity;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;

/**
 * Listener for {@link EntityDefinitionService} events
 * 
 * @author Simon Templer
 */
public interface EntityDefinitionServiceListener {

	/**
	 * Called when a new instance context has been added.
	 * 
	 * @param contextEntity the entity definition representing the instance
	 *            context
	 */
	public void contextAdded(EntityDefinition contextEntity);

	/**
	 * Called when multiple new instance contexts have been added.
	 * 
	 * @param contextEntities the entity definitions representing the instance
	 *            contexts
	 */
	public void contextsAdded(Iterable<EntityDefinition> contextEntities);

	/**
	 * Called when an instance context has been removed.
	 * 
	 * @param contextEntity the entity definition representing the instance
	 *            context
	 */
	public void contextRemoved(EntityDefinition contextEntity);

}
