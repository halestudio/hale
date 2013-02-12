/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.service.values;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;

/**
 * Listener to {@link OccurringValuesService} events.
 * 
 * @author Simon Templer
 */
public interface OccurringValuesListener {

	/**
	 * Called when the occurring values for a property entity definition have
	 * been updated.
	 * 
	 * @param property the property entity definition
	 */
	public void occurringValuesUpdated(PropertyEntityDefinition property);

	/**
	 * Called when the occurring values have been invalidated for a schema
	 * space.
	 * 
	 * @param schemaSpace the schema space
	 */
	public void occurringValuesInvalidated(SchemaSpaceID schemaSpace);

}
