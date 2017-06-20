/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.core.io.supplier;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Output supplier that supports multiple target locations.
 * 
 * @author Florian Esser
 */
public class MultiLocationOutputSupplier extends NoStreamOutputSupplier {

	private final List<URI> locations = new ArrayList<>();

	/**
	 * Create a MultiOutputSupplier for the given locations
	 * 
	 * @param locations the collection of location URIs
	 */
	public MultiLocationOutputSupplier(Collection<URI> locations) {
		super(null);

		this.locations.addAll(locations);
	}

	/**
	 * @return the list of location URIs
	 */
	public List<URI> getLocations() {
		return locations;
	}

}
