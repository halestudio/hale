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
 * Constants for hale connect services
 * 
 * @author Florian Esser
 */
public interface HaleConnectServices {

	/**
	 * User service
	 */
	public static String WEB_CLIENT = "hale-connect";

	/**
	 * User service
	 */
	public static String USER_SERVICE = "user-service";

	/**
	 * Bucket service
	 */
	public static String BUCKET_SERVICE = "bucket-service";

	/**
	 * Project store
	 */
	public static String PROJECT_STORE = "project-store";

	/**
	 * Project service
	 */
	public static String PROJECT_SERVICE = "project-service";
}
