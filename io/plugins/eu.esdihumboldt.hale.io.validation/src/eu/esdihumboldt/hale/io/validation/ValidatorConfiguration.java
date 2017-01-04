/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.io.validation;

import java.net.URI;

/**
 * Representation of a validator configuration
 * 
 * @author Florian Esser
 */
public class ValidatorConfiguration {

	private final String configuration;

	private final URI location;

	/**
	 * Creates a validation configuration
	 * 
	 * @param configuration Validation configuration
	 * @param location the source location of the configuration
	 */
	public ValidatorConfiguration(String configuration, URI location) {
		this.configuration = configuration;
		this.location = location;
	}

	/**
	 * @return the validation configuration
	 */
	public String getConfiguration() {
		return configuration;
	}

	/**
	 * @return the source location of the validation configuration
	 */
	public URI getLocation() {
		return location;
	}
}
