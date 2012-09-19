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

package eu.esdihumboldt.hale.ui.common.service.population.impl;

import java.util.concurrent.CopyOnWriteArraySet;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationListener;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;

/**
 * Base implementation for {@link PopulationService}s.
 * 
 * @author Simon Templer
 */
public abstract class AbstractPopulationService implements PopulationService {

	private final CopyOnWriteArraySet<PopulationListener> listeners = new CopyOnWriteArraySet<PopulationListener>();

	/**
	 * @see PopulationService#addListener(PopulationListener)
	 */
	@Override
	public void addListener(PopulationListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see PopulationService#removeListener(PopulationListener)
	 */
	@Override
	public void removeListener(PopulationListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Called when the population for a schema space has changed.
	 * 
	 * @param ssid the schema space
	 */
	protected void firePopulationChanged(SchemaSpaceID ssid) {
		for (PopulationListener listener : listeners) {
			listener.populationChanged(ssid);
		}
	}

}
