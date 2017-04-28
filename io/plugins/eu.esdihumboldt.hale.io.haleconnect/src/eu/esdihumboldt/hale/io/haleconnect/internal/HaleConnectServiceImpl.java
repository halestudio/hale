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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.haleconnect.api.projectstore.v1.ApiCallback;
import com.haleconnect.api.projectstore.v1.ApiResponse;
import com.haleconnect.api.projectstore.v1.api.BucketsApi;
import com.haleconnect.api.projectstore.v1.api.FilesApi;
import com.haleconnect.api.projectstore.v1.api.PermissionsApi;
import com.haleconnect.api.projectstore.v1.model.BucketDetail;
import com.haleconnect.api.projectstore.v1.model.BucketIdent;
import com.haleconnect.api.projectstore.v1.model.Feedback;
import com.haleconnect.api.projectstore.v1.model.NewBucket;
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
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.io.haleconnect.BasePathManager;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectException;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectOrganisationInfo;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectProjectInfo;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectServiceListener;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectSession;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectUrnBuilder;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectUserInfo;
import eu.esdihumboldt.hale.io.haleconnect.Owner;
import eu.esdihumboldt.hale.io.haleconnect.project.SharingOptions;

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
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#addListener(eu.esdihumboldt.hale.io.haleconnect.HaleConnectServiceListener)
	 */
	@Override
	public void addListener(HaleConnectServiceListener listener) {
		listeners.add(listener);
	}

	@Override
	public void clearSession() {
		this.session = null;
		notifyLoginStateChanged();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#createProject(java.lang.String,
	 *      java.lang.String, eu.esdihumboldt.hale.io.haleconnect.Owner,
	 *      boolean)
	 */
	@Override
	public String createProject(String name, String author, Owner owner, boolean versionControl)
			throws HaleConnectException {

		if (!this.isLoggedIn()) {
			throw new HaleConnectException("Not logged in");
		}

		String apiKey = this.getSession().getToken();

		NewBucket newBucket = new NewBucket();
		newBucket.setName(name);
		newBucket.setVersionControl(versionControl);

		final BucketIdent id;
		try {
			BucketsApi bucketsApi = ProjectStoreHelper.getBucketsApi(this, apiKey);

			// POST /buckets
			id = bucketsApi.createBucket(newBucket, getContextOrganisation());
			Owner bucketOwner = UserServiceHelper.toOwner(id.getUserId(), id.getOrgId());

			// PUT /buckets/{ownerType}/{ownerId}/{bucketID}/p/author
			bucketsApi.setBucketProperty(bucketOwner.getType().getJsonValue(), bucketOwner.getId(),
					id.getTransformationproject(), "author", author);
		} catch (com.haleconnect.api.projectstore.v1.ApiException e) {
			throw new HaleConnectException(e.getMessage(), e);
		}

		return id.getTransformationproject();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.BasePathResolver#getBasePath(String)
	 */
	@Override
	public String getBasePath(String service) {
		return basePaths.get(service);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#getBasePathManager()
	 */
	@Override
	public BasePathManager getBasePathManager() {
		return this;
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

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#getProject(Owner,
	 *      String)
	 */
	@Override
	public HaleConnectProjectInfo getProject(Owner owner, String projectId)
			throws HaleConnectException {
		BucketDetail bucketDetail;
		try {
			bucketDetail = ProjectStoreHelper.getBucketsApi(this, this.getSession().getToken())
					.getBucketInfo(owner.getType().getJsonValue(), owner.getId(), projectId);
		} catch (com.haleconnect.api.projectstore.v1.ApiException e) {
			throw new HaleConnectException(e.getMessage(), e);
		}

		return processBucketDetail(bucketDetail);
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
						public void onDownloadProgress(long bytesRead, long contentLength,
								boolean done) {
							// Ignored
						}

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
					});
		} catch (com.haleconnect.api.projectstore.v1.ApiException e) {
			throw new HaleConnectException(e.getMessage(), e);
		}

		return future;
	}

	@Override
	public HaleConnectSession getSession() {
		return session;
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
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#isLoggedIn()
	 */
	@Override
	public boolean isLoggedIn() {
		return session != null;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#loadProject(Owner,
	 *      String)
	 */
	@Override
	public LocatableInputSupplier<InputStream> loadProject(Owner owner, String projectId)
			throws HaleConnectException {

		if (!isLoggedIn()) {
			throw new IllegalStateException("Not logged in.");
		}

		FilesApi api = ProjectStoreHelper.getFilesApi(this, this.getSession().getToken());
		final ApiResponse<File> response;
		try {
			response = api.getProjectFilesAsZipWithHttpInfo(owner.getType().getJsonValue(),
					owner.getId(), projectId);
		} catch (com.haleconnect.api.projectstore.v1.ApiException e) {
			throw new HaleConnectException(e.getMessage(), e);
		}

		return new DefaultInputSupplier(HaleConnectUrnBuilder.buildProjectUrn(owner, projectId)) {

			@Override
			public InputStream getInput() throws IOException {
				return new BufferedInputStream(new FileInputStream(response.getData()));
			}

		};

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

				// First get the current user's profile to obtain the user ID
				// required to fetch the extended profile (including the user's
				// roles/organisations) in the next step
				UserInfo shortProfile = usersApi.getProfileOfCurrentUser();
				session = new HaleConnectSessionImpl(username, token.getToken(),
						usersApi.getProfile(shortProfile.getId()));
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

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#removeListener(eu.esdihumboldt.hale.io.haleconnect.HaleConnectServiceListener)
	 */
	@Override
	public void removeListener(HaleConnectServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.BasePathManager#setBasePath(String,
	 *      String)
	 */
	@Override
	public void setBasePath(String service, String basePath) {
		if (service == null || basePath == null) {
			throw new NullPointerException("service and basePath must not be null");
		}

		while (basePath.endsWith("/")) {
			basePath = StringUtils.removeEnd(basePath, "/");
		}
		basePaths.put(service, basePath);
	}

	@Override
	public ListenableFuture<Boolean> uploadProjectFileAync(String projectId, Owner owner, File file,
			ProgressIndicator progress) throws HaleConnectException {

		if (!this.isLoggedIn()) {
			throw new HaleConnectException("Not logged in");
		}

		String apiKey = this.getSession().getToken();

		// PUT /buckets/{ownerType}/{ownerId}/{bucketID}/name

		FilesApi filesApi = ProjectStoreHelper.getFilesApi(this, apiKey);

//		refactor to reuse code in both sync and async methods

		SettableFuture<Boolean> future = SettableFuture.create();
		try {

			// POST /raw

			int totalWork = computeTotalWork(file);

			progress.begin("Uploading project archive", totalWork);
			filesApi.addFilesAsync(owner.getType().getJsonValue(), owner.getId(), projectId, file,
					createUploadFileCallback(future, progress, file, totalWork));
		} catch (

		com.haleconnect.api.projectstore.v1.ApiException e) {
			throw new HaleConnectException(e.getMessage(), e);
		}

		return future;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#uploadProjectFile(java.lang.String,
	 *      eu.esdihumboldt.hale.io.haleconnect.Owner, java.io.File,
	 *      eu.esdihumboldt.hale.common.core.io.ProgressIndicator)
	 */
	@Override
	public boolean uploadProjectFile(String projectId, Owner owner, File file,
			ProgressIndicator progress) throws HaleConnectException {

		if (!this.isLoggedIn()) {
			throw new HaleConnectException("Not logged in");
		}

		String apiKey = this.getSession().getToken();

		SettableFuture<Boolean> future = SettableFuture.create();
		try {
			FilesApi filesApi = ProjectStoreHelper.getFilesApi(this, apiKey);

			// POST /raw

			int totalWork = computeTotalWork(file);

			ApiCallback<Feedback> apiCallback = createUploadFileCallback(future, progress, file,
					totalWork);

			progress.begin("Uploading project archive", totalWork);
			filesApi.addFilesAsync(owner.getType().getJsonValue(), owner.getId(), projectId, file,
					apiCallback);

			return future.get();
		} catch (com.haleconnect.api.projectstore.v1.ApiException | InterruptedException
				| ExecutionException e) {
			throw new HaleConnectException(e.getMessage(), e);
		}
	}

	private int computeTotalWork(File file) {
		int totalWork;
		// Support upload progress only for files where its size in
		// KiB fits into an int. Round up to next KiB.
		long sizeKiB = (file.length() >> 10) + 1;
		if (sizeKiB > Integer.MAX_VALUE) {
			totalWork = ProgressIndicator.UNKNOWN;
		}
		else {
			totalWork = Math.toIntExact(sizeKiB);
		}
		return totalWork;
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

	private void notifyLoginStateChanged() {
		for (HaleConnectServiceListener listener : listeners) {
			listener.loginStateChanged(isLoggedIn());
		}
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
				result.add(processBucketDetail(bucket));
			}
		}
		return result;
	}

	private HaleConnectProjectInfo processBucketDetail(BucketDetail bucket) {
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

		return new HaleConnectProjectInfo(bucket.getId().getTransformationproject(), user, org,
				bucket.getName(), author);
	}

	private ApiCallback<Feedback> createUploadFileCallback(final SettableFuture<Boolean> future,
			final ProgressIndicator progress, final File file, final int totalWork) {
		return new ApiCallback<Feedback>() {

			AtomicLong chunkWritten = new AtomicLong(0);
			AtomicLong bytesReported = new AtomicLong(0);

			@Override
			public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {
				// not required
			}

			@Override
			public void onFailure(com.haleconnect.api.projectstore.v1.ApiException e,
					int statusCode, Map<String, List<String>> responseHeaders) {
				progress.end();
				future.setException(
						new HaleConnectException(e.getMessage(), e, statusCode, responseHeaders));
			}

			@Override
			public void onSuccess(Feedback result, int statusCode,
					Map<String, List<String>> responseHeaders) {
				if (result.getError()) {
					log.error(MessageFormat.format("Error uploading project file \"{0}\": {1}",
							file.getAbsolutePath(), result.getMessage()));
					future.set(false);
				}
				else {
					future.set(true);
				}
				progress.end();
			}

			@Override
			public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {
				// bytesWritten contains the accumulated amount of bytes written
				if (totalWork != ProgressIndicator.UNKNOWN) {
					// Wait until at least 1 KiB was written
					long chunk = chunkWritten.get();
					chunk += bytesWritten - bytesReported.get();
					if (chunk >= 1024) {
						long workToReport = chunk >> 10;
						// cannot overflow, total size in KiB
						// is guaranteed to be < Integer.MAX_VALUE
						progress.advance(Math.toIntExact(workToReport));
						chunk -= workToReport << 10;
						// chunkWritten now always < 1024
					}
					chunkWritten.set(chunk);
					bytesReported.set(bytesWritten);
				}
			}
		};
	}

	@Override
	public boolean setProjectSharingOptions(String projectId, Owner owner, SharingOptions options)
			throws HaleConnectException {
		BucketsApi bucketsApi = ProjectStoreHelper.getBucketsApi(this,
				this.getSession().getToken());

		Feedback feedback;
		try {
			feedback = bucketsApi.setBucketProperty(owner.getType().getJsonValue(), owner.getId(),
					projectId, "sharingOptions", options);
		} catch (com.haleconnect.api.projectstore.v1.ApiException e) {
			throw new HaleConnectException(e.getMessage(), e);
		}

		if (feedback.getError()) {
			log.error(MessageFormat.format(
					"Error setting sharing options for hale connect project {0}", projectId));
			return false;
		}

		return true;
	}

	@Override
	public boolean testProjectPermission(String permission, String projectId)
			throws HaleConnectException {
		PermissionsApi api = ProjectStoreHelper.getPermissionsApi(this,
				this.getSession().getToken());
		try {
			api.testBucketPermission(permission, projectId);
		} catch (com.haleconnect.api.projectstore.v1.ApiException e) {
			if (e.getCode() == 403) {
				// not allowed
				return false;
			}

			// other codes indicate client error or server-side exception
			throw new HaleConnectException(e.getMessage(), e);
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean testUserPermission(String resourceType, String role, String permission)
			throws HaleConnectException {
		com.haleconnect.api.user.v1.api.PermissionsApi api = UserServiceHelper
				.getPermissionsApi(this, this.getSession().getToken());

		try {
			Map<String, Object> permissions = (Map<String, Object>) api
					.getResourcePermissionInfo(resourceType, permission);
			if ("user".equals(role)) {
				Object userPermission = permissions.get("user");
				return "true".equals(userPermission.toString());
			}
			else {
				// Interpret role as orgId
				Object orgPermission = permissions.get("organisations");
				if (orgPermission instanceof Map) {
					// keySet is set of organisation ids
					Map<String, Object> orgPermissions = (Map<String, Object>) orgPermission;
					Object conditions = Optional.ofNullable(orgPermissions.get(role))
							.orElse(Collections.EMPTY_LIST);
					if ("false".equals(conditions.toString())) {
						return false;
					}
					else if (conditions instanceof List) {
						return ((List<?>) conditions).stream()
								.anyMatch(cond -> "organisation".equals(cond.toString()));
					}
				}
			}
		} catch (ApiException e) {
			throw new HaleConnectException(e.getMessage(), e);
		}

		return false;
	}

}
