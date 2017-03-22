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

package eu.esdihumboldt.hale.io.haleconnect;

/**
 * hale connect service interface
 * 
 * @author Florian Esser
 */
public interface HaleConnectService {

	/**
	 * Login to hale connect
	 * 
	 * @param username user name
	 * @param password password
	 * @return true, if the login attempt was successful, false otherwise
	 * @throws HaleConnectException thrown on any API errors that do not simply
	 *             indicate invalid credentials
	 */
	boolean login(String username, String password) throws HaleConnectException;

	/**
	 * Verify that the given credentials are valid
	 * 
	 * @param username user name
	 * @param password password
	 * @return true, if the credentials were accepted, false otherwise
	 * @throws HaleConnectException thrown on any API errors that do not simply
	 *             indicate invalid credentials
	 */
	boolean verifyCredentials(String username, String password) throws HaleConnectException;

	/**
	 * @return the currently active session or null
	 */
	HaleConnectSession getSession();

	/**
	 * Deletes all session information
	 */
	void clearSession();

	/**
	 * @return true if a login token was issued by hale connect
	 */
	boolean isLoggedIn();

	/**
	 * Set the base path to the hale connect user service (e.g.
	 * "https://users.haleconnect.com/v1")
	 * 
	 * @param basePath Base path to set
	 */
	void setBasePath(String basePath);

	/**
	 * Adds a listener
	 * 
	 * @param listener the listener to add
	 */
	void addListener(HaleConnectServiceListener listener);

	/**
	 * Removes a listener
	 * 
	 * @param listener the listener to remove
	 */
	void removeListener(HaleConnectServiceListener listener);

}
