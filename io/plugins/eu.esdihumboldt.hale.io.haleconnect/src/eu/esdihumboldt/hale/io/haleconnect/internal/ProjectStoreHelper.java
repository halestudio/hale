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

import java.util.HashMap;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.haleconnect.api.projectstore.v1.ApiClient;
import com.haleconnect.api.projectstore.v1.ApiResponse;
import com.haleconnect.api.projectstore.v1.api.BucketsApi;
import com.haleconnect.api.projectstore.v1.api.FilesApi;
import com.haleconnect.api.projectstore.v1.api.PermissionsApi;
import com.haleconnect.api.projectstore.v1.model.Feedback;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import eu.esdihumboldt.hale.io.haleconnect.BasePathResolver;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectException;
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
		ApiClientHelper.setApiClientProperties(apiClient, HaleConnectServices.PROJECT_STORE,
				resolver, apiKey);

		apiClient.getHttpClient().setConnectionSpecs(ApiClientHelper.buildConnectionSpec());
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

	/**
	 * Execute a REST call with the content type <code>text/plain; charset=utf-8
	 * </code>
	 * 
	 * @param method HTTP method (POST, PUT)
	 * @param path REST path (without base path)
	 * @param body The plain text bdoy
	 * @param basePath The REST base path
	 * @param apiKey The API key
	 * @return the feedback
	 * @throws HaleConnectException thrown on any API error
	 */
	public static Feedback executePlainTextCallWithFeedback(String method, String path, String body,
			BasePathResolver basePath, String apiKey) throws HaleConnectException {
		ApiClient apiClient = ProjectStoreHelper.getApiClient(basePath, apiKey);
		OkHttpClient httpClient = apiClient.getHttpClient();
		String url = apiClient.buildUrl(path, null);
		Request.Builder reqBuilder = new Request.Builder().url(url);

		Map<String, String> headerParams = new HashMap<String, String>();
		apiClient.updateParamsForAuth(new String[] { "bearer" }, null, headerParams);
		apiClient.processHeaderParams(headerParams, reqBuilder);
		RequestBody reqBody = RequestBody.create(MediaType.parse("text/plain; charset=utf-8"),
				body);
		Request request = reqBuilder.method(method, reqBody).build();
		Call call = httpClient.newCall(request);

		Feedback feedback;
		try {
			ApiResponse<Feedback> resp = apiClient.execute(call, new TypeToken<Feedback>() {
				//
			}.getType());
			feedback = resp.getData();
		} catch (com.haleconnect.api.projectstore.v1.ApiException e) {
			throw new HaleConnectException(e.getMessage(), e);
		}

		return feedback;
	}
}
