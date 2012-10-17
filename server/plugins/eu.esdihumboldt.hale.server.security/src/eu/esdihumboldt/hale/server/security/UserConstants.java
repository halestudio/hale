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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.security;

/**
 * User security related constants.
 * 
 * @author Simon Templer
 */
public interface UserConstants {

	/**
	 * Superuser role
	 */
	public static final String ROLE_ADMIN = "ROLE_ADMIN";

	/**
	 * Ordinary user role
	 */
	public static final String ROLE_USER = "ROLE_USER";

	/**
	 * Anonymous user role
	 */
	public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

}
