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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.lang.StringUtils;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.haleconnect.api.projectstore.v1.ApiCallback;
import com.haleconnect.api.projectstore.v1.model.BucketDetail;
import com.haleconnect.api.user.v1.ApiException;
import com.haleconnect.api.user.v1.api.LoginApi;
import com.haleconnect.api.user.v1.api.OrganisationsApi;
import com.haleconnect.api.user.v1.api.UsersApi;
import com.haleconnect.api.user.v1.model.Credentials;
import com.haleconnect.api.user.v1.model.OrganisationInfo;
import com.haleconnect.api.user.v1.model.Token;
import com.haleconnect.api.user.v1.model.UserInfo;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.io.haleconnect.BasePathManager;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectException;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectInputSupplier;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectOrganisationInfo;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectProjectInfo;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectServiceListener;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectSession;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectUserInfo;
import eu.esdihumboldt.hale.io.haleconnect.Owner;

/**
 * hale connect service facade implementation
 * 
 * @author Florian Esser
 */
public class HaleConnectServiceImpl implements HaleConnectService, BasePathManager {

	private static final ALogger log = ALoggerFactory.getLogger(HaleConnectServiceImpl.class);

	private final CopyOnWriteArraySet<HaleConnectServiceListener> listeners = new CopyOnWriteArraySet<HaleConnectServiceListener>();
	private final ConcurrentHashMap<String, String> basePaths = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, HaleConnectUserInfo> userInfoCache = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, HaleConnectOrganisationInfo> orgInfoCache = new ConcurrentHashMap<>();

	private HaleConnectSession session;

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#getBasePathManager()
	 */
	@Override
	public BasePathManager getBasePathManager() {
		return this;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#login(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean login(String username, String password) throws HaleConnectException {
		LoginApi loginApi = UserServiceHelper.getLoginApi(this);
		Credentials credentials = UserServiceHelper.buildCredentials(username, password);

		try {
			Token token = loginApi.login(credentials);
			if (token != null) {
				UsersApi usersApi = UserServiceHelper.getUsersApi(this, token.getToken());
				session = new HaleConnectSessionImpl(username, token.getToken(),
						usersApi.getProfile(username));
				notifyLoginStateChanged();
			}
			else {
				clearSession();
			}
		} catch (ApiException e) {
			if (e.getCode() == 401) {
				clearSession();
			}
			else {
				throw new HaleConnectException(e.getMessage(), e);
			}
		}

		return isLoggedIn();
	}

	@Override
	public boolean verifyCredentials(String username, String password) throws HaleConnectException {
		try {
			return UserServiceHelper.getLoginApi(this)
					.login(UserServiceHelper.buildCredentials(username, password)) != null;
		} catch (ApiException e) {
			if (e.getCode() == 401) {
				return false;
			}
			else {
				throw new HaleConnectException(e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 */
	private void notifyLoginStateChanged() {
		for (HaleConnectServiceListener listener : listeners) {
			listener.loginStateChanged(isLoggedIn());
		}
	}

	@Override
	public HaleConnectSession getSession() {
		return session;
	}

	@Override
	public void clearSession() {
		this.session = null;
		notifyLoginStateChanged();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#isLoggedIn()
	 */
	@Override
	public boolean isLoggedIn() {
		return session != null;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#addListener(eu.esdihumboldt.hale.io.haleconnect.HaleConnectServiceListener)
	 */
	@Override
	public void addListener(HaleConnectServiceListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#removeListener(eu.esdihumboldt.hale.io.haleconnect.HaleConnectServiceListener)
	 */
	@Override
	public void removeListener(HaleConnectServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.BasePathResolver#getBasePath(String)
	 */
	@Override
	public String getBasePath(String service) {
		return basePaths.get(service);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.BasePathManager#setBasePath(String,
	 *      String)
	 */
	@Override
	public void setBasePath(String service, String basePath) {
		basePaths.put(service, basePath);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#getProjects()
	 */
	@Override
	public List<HaleConnectProjectInfo> getProjects() throws HaleConnectException {
		List<BucketDetail> bucketDetails;
		try {
			bucketDetails = ProjectStoreHelper.getBucketsApi(this, this.getSession().getToken())
					.getBuckets(null, true);
		} catch (com.haleconnect.api.projectstore.v1.ApiException e) {
			throw new HaleConnectException(e.getMessage(), e);
		}

		return processBucketDetails(bucketDetails);
	}

	@Override
	public ListenableFuture<List<HaleConnectProjectInfo>> getProjectsAsync()
			throws HaleConnectException {
		final SettableFuture<List<HaleConnectProjectInfo>> future = SettableFuture.create();
		try {
			ProjectStoreHelper.getBucketsApi(this, this.getSession().getToken()).getBucketsAsync(
					getContextOrganisation(), true, new ApiCallback<List<BucketDetail>>() {

						@Override
						public void onFailure(com.haleconnect.api.projectstore.v1.ApiException e,
								int statusCode, Map<String, List<String>> responseHeaders) {
							future.setException(new HaleConnectException(e.getMessage(), e,
									statusCode, responseHeaders));
						}

						@Override
						public void onSuccess(List<BucketDetail> result, int statusCode,
								Map<String, List<String>> responseHeaders) {
							future.set(Collections.unmodifiableList(processBucketDetails(result)));
						}

						@Override
						public void onUploadProgress(long bytesWritten, long contentLength,
								boolean done) {
							// Ignored
						}

						@Override
						public void onDownloadProgress(long bytesRead, long contentLength,
								boolean done) {
							// Ignored
						}
					});
		} catch (com.haleconnect.api.projectstore.v1.ApiException e) {
			throw new HaleConnectException(e.getMessage(), e);
		}

		return future;
	}

	/**
	 * Convert a list of {@link BucketDetail}s received from the project store
	 * to a list of {@link HaleConnectProjectInfo}
	 * 
	 * @param bucketDetails bucket details
	 * @return list of hale connect project info
	 */
	private List<HaleConnectProjectInfo> processBucketDetails(List<BucketDetail> bucketDetails) {
		List<HaleConnectProjectInfo> result = new ArrayList<>();
		for (BucketDetail bucket : bucketDetails) {
			if (bucket.getId() != null) {
				String author = null;
				if (bucket.getProperties() instanceof Map<?, ?>) {
					@SuppressWarnings("unchecked")
					Map<Object, Object> properties = (Map<Object, Object>) bucket.getProperties();
					if (properties.containsKey("author")) {
						author = properties.get("author").toString();
					}
				}

				HaleConnectUserInfo user = null;
				HaleConnectOrganisationInfo org = null;
				try {
					if (!StringUtils.isEmpty(bucket.getId().getUserId())) {
						user = this.getUserInfo(bucket.getId().getUserId());
					}

					if (!StringUtils.isEmpty(bucket.getId().getOrgId())) {
						org = this.getOrganisationInfo(bucket.getId().getOrgId());
					}
				} catch (HaleConnectException e) {
					log.error(e.getMessage(), e);
				}

				result.add(new HaleConnectProjectInfo(bucket.getId().getTransformationproject(),
						user, org, bucket.getName(), author));
			}
		}
		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#loadProject(Owner,
	 *      String)
	 */
	@Override
	public LocatableInputSupplier<InputStream> loadProject(Owner owner, String projectId) {
		return new HaleConnectInputSupplier(projectId, owner,
				this.getBasePath(HaleConnectServices.PROJECT_STORE), this.getSession().getToken());
	}

	private String getContextOrganisation() {
		if (!this.isLoggedIn()) {
			return null;
		}

		List<String> orgIds = this.getSession().getOrganisationIds();
		if (orgIds.isEmpty()) {
			return null;
		}

		// XXX Cannot handle multiple organisations!
		return orgIds.iterator().next();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#getUserInfo(java.lang.String)
	 */
	@Override
	public HaleConnectUserInfo getUserInfo(String userId) throws HaleConnectException {
		if (!this.isLoggedIn()) {
			return null;
		}

		if (!userInfoCache.containsKey(userId)) {
			UsersApi api = UserServiceHelper.getUsersApi(this, this.getSession().getToken());
			try {
				UserInfo info = api.getProfile(userId);
				userInfoCache.put(info.getId(), new HaleConnectUserInfo(info.getId(),
						info.getScreenName(), info.getFullName()));
			} catch (ApiException e) {
				throw new HaleConnectException(e.getMessage(), e);
			}
		}

		return userInfoCache.get(userId);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#getOrganisationInfo(java.lang.String)
	 */
	@Override
	public HaleConnectOrganisationInfo getOrganisationInfo(String orgId)
			throws HaleConnectException {

		if (!this.isLoggedIn()) {
			return null;
		}

		if (!orgInfoCache.containsKey(orgId)) {
			OrganisationsApi api = UserServiceHelper.getOrganisationsApi(this,
					this.getSession().getToken());
			try {
				OrganisationInfo org = api.getOrganisation(orgId);
				orgInfoCache.put(org.getId(),
						new HaleConnectOrganisationInfo(org.getId(), org.getName()));
			} catch (ApiException e) {
				throw new HaleConnectException(e.getMessage(), e);
			}
		}

		return orgInfoCache.get(orgId);
	}

}
