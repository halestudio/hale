/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Basic {@link UpdateMessage} implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractUpdateService implements UpdateService {
	
	private final Set<HaleServiceListener> listeners = new HashSet<HaleServiceListener>();

	/**
	 * @see UpdateService#addListener(HaleServiceListener)
	 */
	@Override
	public void addListener(HaleServiceListener listener) {
		synchronized (listeners) {
			this.listeners.add(listener);
		}
	}

	/**
	 * @see UpdateService#removeListener(HaleServiceListener)
	 */
	@Override
	public void removeListener(HaleServiceListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	/**
	 * Inform {@link HaleServiceListener}s of an update.
	 * 
	 * @param message the update message 
	 */
	protected void notifyListeners(UpdateMessage<?> message) {
		for (HaleServiceListener hsl : getListeners()) {
			hsl.update(message);
		}
	}

	/**
	 * Get a copy of the listener collection
	 * 
	 * @return a copy of the listener collection
	 */
	protected Iterable<HaleServiceListener> getListeners() {
		Collection<HaleServiceListener> tmp;
		synchronized (listeners) {
			tmp = new ArrayList<HaleServiceListener>(listeners);
		}
		return tmp;
	}

}
