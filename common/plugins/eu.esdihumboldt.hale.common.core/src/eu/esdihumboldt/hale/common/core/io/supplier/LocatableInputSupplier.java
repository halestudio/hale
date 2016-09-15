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

package eu.esdihumboldt.hale.common.core.io.supplier;

import java.net.URI;

import eu.esdihumboldt.util.io.InputSupplier;

/**
 * Locatable {@link InputSupplier} providing an URI as location
 * 
 * @param <T> the input type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface LocatableInputSupplier<T> extends InputSupplier<T>, Locatable {

	// combines interfaces InputSupplier and Locatable

	/**
	 * Returns the used location, which may be relative.
	 * 
	 * @return the used location; either the same as {@link #getLocation()}, or
	 *         a relative URI
	 */
	public URI getUsedLocation();

}
