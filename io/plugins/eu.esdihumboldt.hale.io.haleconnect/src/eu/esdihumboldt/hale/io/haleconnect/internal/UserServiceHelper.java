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

import java.util.Optional;

import org.apache.commons.lang.StringUtils;

import com.haleconnect.api.user.v1.ApiClient;
import com.haleconnect.api.user.v1.api.LoginApi;
import com.haleconnect.api.user.v1.api.OrganisationsApi;
import com.haleconnect.api.user.v1.api.PermissionsApi;
import com.haleconnect.api.user.v1.api.UsersApi;
import com.haleconnect.api.user.v1.model.Credentials;

import eu.esdihumboldt.hale.io.haleconnect.BasePathResolver;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices;
import eu.esdihumboldt.hale.io.haleconnect.Owner;
import eu.esdihumboldt.hale.io.haleconnect.OwnerType;

/**
 * Helper class for the user service API
 * 
 * @author Florian Esser
 */
public class UserServiceHelper {

	/**
	 * Build a {@link Credentials} object. Any null values passed in will be
	 * converted to an empty string.
	 * 
	 * @param username the user name
	 * @param password the password
	 * @return a Credentials object with the given credentials
	 */
	public static Credentials buildCredentials(String username, String password) {
		Credentials credentials = new Credentials();
		credentials.setUsername(Optional.ofNullable(username).orElse(""));
		credentials.setPassword(Optional.ofNullable(password).orElse(""));
		return credentials;
	}

	/**
	 * @param resolver the base path resolver
	 * @param apiKey JWT for authentication
	 * @return ApiClient for the user service
	 */
	public static ApiClient getApiClient(BasePathResolver resolver, String apiKey) {
		ApiClient apiClient = new ApiClient();
		ApiClientHelper.setApiClientProperties(apiClient, HaleConnectServices.USER_SERVICE,
				resolver, apiKey);

		return apiClient;
	}

	/**
	 * @param resolver the base path resolver
	 * @return user service's Login API
	 */
	public static LoginApi getLoginApi(BasePathResolver resolver) {
		return new LoginApi(getApiClient(resolver, null));
	}

	/**
	 * @param resolver the base path resolver
	 * @param apiKey JWT for authentication
	 * @return user service's Users API
	 */
	public static UsersApi getUsersApi(BasePathResolver resolver, String apiKey) {
		return new UsersApi(getApiClient(resolver, apiKey));
	}

	/**
	 * @param resolver the base path resolver
	 * @param apiKey JWT for authentication
	 * @return user service's Organisations API
	 */
	public static OrganisationsApi getOrganisationsApi(BasePathResolver resolver, String apiKey) {
		return new OrganisationsApi(getApiClient(resolver, apiKey));
	}

	/**
	 * Helper method to create an {@link Owner} if an API call returns both a
	 * user ID and an organisation ID, and the owner type is determined by which
	 * of the values is non-empty.
	 * 
	 * @param userId user id value
	 * @param orgId organisation id value
	 * @return Owner object
	 * @throws IllegalArgumentException thrown if none or both of userId and
	 *             orgId are non-empty.
	 */
	public static Owner toOwner(String userId, String orgId) throws IllegalArgumentException {
		boolean isUser = !StringUtils.isEmpty(userId);
		boolean isOrg = !StringUtils.isEmpty(orgId);

		if (isUser == isOrg) {
			throw new IllegalArgumentException("Exactly one of userId, orgId must be non-empty");
		}

		if (isUser) {
			return new Owner(OwnerType.USER, userId);
		}
		else {
			return new Owner(OwnerType.ORGANISATION, orgId);
		}
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
