/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.io.supplier;

import java.net.URI;

/**
 * Simple helper when a {@link Locatable} object is needed, that only represents
 * the corresponding URI.
 * 
 * @author Simon Templer
 */
public class LocatableURI implements Locatable {

	private final URI location;

	/**
	 * Constructor.
	 * 
	 * @param location the location to be represented by this locatable object
	 */
	public LocatableURI(URI location) {
		super();
		this.location = location;
	}

	@Override
	public URI getLocation() {
		return location;
	}

}
