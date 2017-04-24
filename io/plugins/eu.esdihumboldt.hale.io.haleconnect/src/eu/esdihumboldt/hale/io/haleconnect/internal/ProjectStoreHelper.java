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

import com.haleconnect.api.projectstore.v1.ApiClient;
import com.haleconnect.api.projectstore.v1.api.BucketsApi;
import com.haleconnect.api.projectstore.v1.api.FilesApi;
import com.haleconnect.api.projectstore.v1.api.PermissionsApi;

import eu.esdihumboldt.hale.io.haleconnect.BasePathResolver;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices;

/**
 * Helper class for the project store API
 * 
 * @author Florian Esser
 */
public class ProjectStoreHelper {

	/**
	 * @param resolver the base path resolver
	 * @param apiKey JWT for authentication
	 * @return ApiClient for the project store
	 */
	public static ApiClient getApiClient(BasePathResolver resolver, String apiKey) {
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(resolver.getBasePath(HaleConnectServices.PROJECT_STORE));
		apiClient.setApiKey(apiKey);
		apiClient.setApiKeyPrefix("Bearer");
		return apiClient;
	}

	/**
	 * @param resolver the base path resolver
	 * @param apiKey JWT for authentication
	 * @return project store's Buckets API
	 */
	public static BucketsApi getBucketsApi(BasePathResolver resolver, String apiKey) {
		return new BucketsApi(getApiClient(resolver, apiKey));
	}

	/**
	 * @param basePath the base path
	 * @param apiKey JWT for authentication
	 * @return project store's Buckets API
	 */
	public static BucketsApi getBucketsApi(final String basePath, String apiKey) {
		return new BucketsApi(getApiClient(new BasePathResolver() {

			@Override
			public String getBasePath(String service) {
				return basePath;
			}
		}, apiKey));
	}

	/**
	 * @param resolver the base path resolver
	 * @param apiKey JWT for authentication
	 * @return project store's Files API
	 */
	public static FilesApi getFilesApi(BasePathResolver resolver, String apiKey) {
		return new FilesApi(getApiClient(resolver, apiKey));
	}

	/**
	 * @param basePath the base path
	 * @param apiKey JWT for authentication
	 * @return project store's Files API
	 */
	public static FilesApi getFilesApi(String basePath, String apiKey) {
		return new FilesApi(getApiClient(new BasePathResolver() {

			@Override
			public String getBasePath(String service) {
				return basePath;
			}
		}, apiKey));
	}

	/**
	 * @param resolver the base path resolver
	 * @param apiKey JWT for authentication
	 * @return project store's Files API
	 */
	public static PermissionsApi getPermissionsApi(BasePathResolver resolver, String apiKey) {
		return new PermissionsApi(getApiClient(resolver, apiKey));
	}

	/**
	 * @param basePath the base path
	 * @param apiKey JWT for authentication
	 * @return project store's Files API
	 */
	public static PermissionsApi getPermissionsApi(String basePath, String apiKey) {
		return new PermissionsApi(getApiClient(new BasePathResolver() {

			@Override
			public String getBasePath(String service) {
				return basePath;
			}
		}, apiKey));
	}

}
