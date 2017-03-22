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
 * Details of a hale connect session
 * 
 * @author Florian Esser
 */
public interface HaleConnectSession {

	/**
	 * @return User name that was used to log in to hale connect
	 */
	String getUsername();

	/**
	 * @return JSON Web Token associated with the session
	 */
	String getToken();
}
