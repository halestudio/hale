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

package eu.esdihumboldt.hale.common.core.io.config;

/**
 * Common username and password credentials configuration constants.
 * 
 * @author Simon Templer
 */
public interface UserPasswordCredentials {

	/**
	 * Name of the configuration parameter specifying the user name.
	 */
	static final String PARAM_USER = "credentials.user";

	/**
	 * Name of the configuration parameter specifying the password.
	 */
	static final String PARAM_PASSWORD = "credentials.password";

}
