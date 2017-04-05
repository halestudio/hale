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

package eu.esdihumboldt.hale.io.haleconnect.internal;

import com.haleconnect.api.project.v0_1.ApiClient;
import com.haleconnect.api.project.v0_1.api.ProjectApi;

import eu.esdihumboldt.hale.io.haleconnect.BasePathResolver;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices;

/**
 * Helper class for the project service API
 * 
 * @author Florian Esser
 */
public class ProjectServiceHelper {

	/**
	 * @param resolver the base path resolver
	 * @param apiKey JWT for authentication
	 * @return ApiClient for the project store
	 */
	public static ApiClient getApiClient(BasePathResolver resolver, String apiKey) {
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(resolver.getBasePath(HaleConnectServices.BUCKET_SERVICE));
		apiClient.setApiKey(apiKey);
		apiClient.setApiKeyPrefix("Bearer");
		return apiClient;
	}

	/**
	 * @param resolver the base path resolver
	 * @param apiKey JWT for authentication
	 * @return project store's Projects API
	 */
	public static ProjectApi getProjectApi(BasePathResolver resolver, String apiKey) {
		return new ProjectApi(getApiClient(resolver, apiKey));
	}

}
