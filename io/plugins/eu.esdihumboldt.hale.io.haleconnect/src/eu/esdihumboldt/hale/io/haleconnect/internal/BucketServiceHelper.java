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

import com.haleconnect.api.bucket.v1.ApiClient;
import com.haleconnect.api.bucket.v1.api.BucketsApi;

import eu.esdihumboldt.hale.io.haleconnect.BasePathResolver;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices;

/**
 * Helper class for the bucket service API
 * 
 * @author Florian Esser
 */
public class BucketServiceHelper {

	/**
	 * @param resolver the base path resolver
	 * @param apiKey JWT for authentication
	 * @return ApiClient for the bucket service
	 */
	public static ApiClient getApiClient(BasePathResolver resolver, String apiKey) {
		ApiClient apiClient = new ApiClient();
		ApiClientHelper.setApiClientProperties(apiClient, HaleConnectServices.BUCKET_SERVICE,
				resolver, apiKey);
		apiClient.getHttpClient().setConnectionSpecs(ApiClientHelper.buildConnectionSpec());
		return apiClient;
	}

	/**
	 * @param resolver the base path resolver
	 * @param apiKey JWT for authentication
	 * @return bucket service's Buckets API
	 */
	public static BucketsApi getBucketsApi(BasePathResolver resolver, String apiKey) {
		return new BucketsApi(getApiClient(resolver, apiKey));
	}

}
