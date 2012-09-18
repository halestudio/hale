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

package eu.esdihumboldt.hale.common.core.io;

/**
 * Exception that is thrown when an I/O provider has not been configured
 * properly
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public class IOProviderConfigurationException extends Exception {

	private static final long serialVersionUID = -2422941090415844659L;

	/**
	 * @see Exception#Exception()
	 */
	public IOProviderConfigurationException() {
		super();
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public IOProviderConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public IOProviderConfigurationException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public IOProviderConfigurationException(Throwable cause) {
		super(cause);
	}

}
