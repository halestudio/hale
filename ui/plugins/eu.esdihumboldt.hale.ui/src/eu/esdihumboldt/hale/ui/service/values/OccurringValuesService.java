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

/**
 * Service that allows to analyze the values that occur for a specific property
 * entity definition.
 * 
 * @author Simon Templer
 */
public interface OccurringValuesService {

	/**
	 * Get the values occurring in the data for the given property entity.
	 * 
	 * @param property the property entity definition
	 * @return the occurring values for the property or <code>null</code>
	 */
	public OccurringValues getOccurringValues(PropertyEntityDefinition property);

	/**
	 * Update the occurring values for the given property entity.
	 * 
	 * @param property the property entity definition
	 * @return <code>true</code> if the task to update the information has been
	 *         started, <code>false</code> if the information was up-to-date or
	 *         cannot be determined for the given property
	 * @throws IllegalArgumentException if determining the occurring values is
	 *             not supported for the given property, i.e. if it passes
	 *             {@link OccurringValuesUtil#supportsOccurringValues(PropertyEntityDefinition)}
	 */
	public boolean updateOccurringValues(PropertyEntityDefinition property)
			throws IllegalArgumentException;

	/**
	 * Add a listener to the service.
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(OccurringValuesListener listener);

	/**
	 * Remove a listener from the service.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(OccurringValuesListener listener);

}
