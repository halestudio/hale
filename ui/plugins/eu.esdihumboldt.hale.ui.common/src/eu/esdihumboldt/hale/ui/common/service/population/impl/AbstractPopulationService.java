/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.common.service.population.impl;

import de.cs3d.util.eclipse.TypeSafeListenerList;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationListener;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;

/**
 * Base implementation for {@link PopulationService}s.
 * 
 * @author Simon Templer
 */
public abstract class AbstractPopulationService implements PopulationService {

	private TypeSafeListenerList<PopulationListener> listeners = new TypeSafeListenerList<PopulationListener>();

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
