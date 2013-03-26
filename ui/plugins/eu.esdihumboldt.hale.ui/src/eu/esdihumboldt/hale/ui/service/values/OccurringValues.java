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

import com.google.common.collect.Multiset;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;

/**
 * Holds information about the occurring values in a specific
 * {@link PropertyEntityDefinition}.
 * 
 * @author Simon Templer
 */
public interface OccurringValues {

	/**
	 * Get the occurring values for the associated property entity.
	 * 
	 * @return the set of different values
	 */
	public Multiset<Object> getValues();

	/**
	 * Get the associated property entity.
	 * 
	 * @return the property entity definition for which the values occur
	 */
	public PropertyEntityDefinition getProperty();

	/**
	 * Specifies if the values are up-to-date or if there may be discrepancies
	 * to the current loaded instances (e.g. if new instances have been added
	 * since the calculation of these values).
	 * 
	 * @return <code>true</code> if it can be safely assumed that the
	 *         information is up-to-date, <code>false</code> otherwise
	 */
	public boolean isUpToDate();

}
