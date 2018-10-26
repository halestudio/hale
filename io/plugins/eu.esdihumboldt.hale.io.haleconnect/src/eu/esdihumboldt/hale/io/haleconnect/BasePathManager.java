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

import static eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices.BUCKET_SERVICE;
import static eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices.HALE_CONNECT_BASE_URL_DEFAULT;
import static eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices.HALE_CONNECT_PATH_CLIENT_DEFAULT;
import static eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices.HALE_CONNECT_PATH_DATA_DEFAULT;
import static eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices.HALE_CONNECT_PATH_PROJECTS_DEFAULT;
import static eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices.HALE_CONNECT_PATH_USERS_DEFAULT;
import static eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices.PROJECT_STORE;
import static eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices.USER_SERVICE;
import static eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices.WEB_CLIENT;

/**
 * Interface for hale connect base path managers.
 * 
 * @author Florian Esser
 */
public interface BasePathManager extends BasePathResolver {

	/**
	 * Set the base path of a hale connect microservice (e.g.
	 * "https://users.haleconnect.com/v1" for the user service)
	 *
	 * @param service service to set the base path for, usually one of the
	 *            constants defined in {@link HaleConnectServices}
	 * @param basePath Base path to set
	 */
	void setBasePath(String service, String basePath);

	/**
	 * Set the base URL for a hale connect installation, assuming it uses the
	 * default paths for services.
	 * 
	 * @param baseUrl the base URL for the installation, e.g.
	 *            {@value HaleConnectServices#HALE_CONNECT_BASE_URL_DEFAULT}
	 */
	default void setBaseUrl(String baseUrl) {
		setBasePath(USER_SERVICE, baseUrl + HALE_CONNECT_PATH_USERS_DEFAULT);
		setBasePath(BUCKET_SERVICE, baseUrl + HALE_CONNECT_PATH_DATA_DEFAULT);
		setBasePath(PROJECT_STORE, baseUrl + HALE_CONNECT_PATH_PROJECTS_DEFAULT);
		setBasePath(WEB_CLIENT, baseUrl + HALE_CONNECT_PATH_CLIENT_DEFAULT);
	}

	/**
	 * Set the default base paths for using haleconnect.com
	 */
	default void setDefaults() {
		setBaseUrl(HALE_CONNECT_BASE_URL_DEFAULT);
	}

}
