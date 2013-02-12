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

import java.util.concurrent.CopyOnWriteArraySet;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.service.values.OccurringValuesListener;
import eu.esdihumboldt.hale.ui.service.values.OccurringValuesService;

/**
 * Base class for {@link OccurringValuesService} implementations.
 * 
 * @author Simon Templer
 */
public abstract class AbstractOccurringValuesService implements OccurringValuesService {

	private final CopyOnWriteArraySet<OccurringValuesListener> listeners = new CopyOnWriteArraySet<OccurringValuesListener>();

	@Override
	public void addListener(OccurringValuesListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(OccurringValuesListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Called when the occurring values for a property entity definition have
	 * been updated.
	 * 
	 * @param property the property entity definition
	 */
	public void notifyOccurringValuesUpdated(PropertyEntityDefinition property) {
		for (OccurringValuesListener listener : listeners) {
			listener.occurringValuesUpdated(property);
		}
	}

	/**
	 * Called when the occurring values have been invalidated for a schema
	 * space.
	 * 
	 * @param schemaSpace the schema space
	 */
	public void notifyOccurringValuesInvalidated(SchemaSpaceID schemaSpace) {
		for (OccurringValuesListener listener : listeners) {
			listener.occurringValuesInvalidated(schemaSpace);
		}
	}

}
