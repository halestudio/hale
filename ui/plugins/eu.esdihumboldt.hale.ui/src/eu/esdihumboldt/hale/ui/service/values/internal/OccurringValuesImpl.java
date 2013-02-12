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

package eu.esdihumboldt.hale.ui.service.values.internal;

import java.util.Collections;
import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.ui.service.values.OccurringValues;

/**
 * Default {@link OccurringValues} implementation. Allows updating the
 * information if the object is up-to-date.
 * 
 * @author Simon Templer
 */
public class OccurringValuesImpl implements OccurringValues {

	private volatile boolean upToDate;

	private final Set<Object> values;

	private final PropertyEntityDefinition property;

	/**
	 * Create an object with information about the occurring values in a
	 * property.
	 * 
	 * @param values the occurring values
	 * @param property the property
	 */
	public OccurringValuesImpl(Set<Object> values, PropertyEntityDefinition property) {
		super();
		this.upToDate = true;
		this.values = Collections.unmodifiableSet(values);
		this.property = property;
	}

	@Override
	public Set<Object> getValues() {
		return values;
	}

	@Override
	public PropertyEntityDefinition getProperty() {
		return property;
	}

	@Override
	public boolean isUpToDate() {
		return upToDate;
	}

	/**
	 * Invalidate the information.
	 */
	public void invalidate() {
		this.upToDate = false;
	}

}
