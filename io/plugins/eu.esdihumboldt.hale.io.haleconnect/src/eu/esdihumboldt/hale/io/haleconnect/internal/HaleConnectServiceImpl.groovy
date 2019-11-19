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

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.haleconnect.api.projectstore.v1.ApiCallback;
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
import com.haleconnect.api.user.v1.model.OrganisationInfo
import com.haleconnect.api.user.v1.model.Token;
import com.haleconnect.api.user.v1.model.UserInfo;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.io.haleconnect.BasePathManager;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectException;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectInputSupplier;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectOrganisationInfo;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectProjectInfo;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectServiceListener;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectSession;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectUrnBuilder;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectUserInfo;
import eu.esdihumboldt.hale.io.haleconnect.Owner;
import eu.esdihumboldt.hale.io.haleconnect.project.SharingOptions;
import groovy.transform.CompileStatic

/**
 * hale connect service facade implementation
 * 
 * @author Florian Esser
 */
@CompileStatic
public class HaleConnectServiceImpl implements HaleConnectService, BasePathManager {

	private static final ALogger log = ALoggerFactory.getLogger(HaleConnectServiceImpl.class);

	private final CopyOnWriteArraySet<HaleConnectServiceListener> listeners = new CopyOnWriteArraySet<HaleConnectServiceListener>();
	private final ConcurrentHashMap<String, String> basePaths = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, HaleConnectUserInfo> userInfoCache = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, HaleConnectOrganisationInfo> orgInfoCache = new ConcurrentHashMap<>();

	private HaleConnectSession session;

	/**
	 * Default constructor.
	 */
	public HaleConnectServiceImpl() {
		super();

		// store default values for base paths
		// in the UI will this will be overridden in HaleConnectUIPlugin
		getBasePathManager().setDefaults();
	}

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
		newBucket.setDriver(versionControl ? "git" : "s3");

		final BucketIdent id;
		try {
			BucketsApi bucketsApi = ProjectStoreHelper.getBucketsApi(this, apiKey);

			// POST /buckets
			id = bucketsApi.createBucketWithOwner(owner.getType().getJsonValue(), owner.getId(),
					newBucket);
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
			return HaleConnectOrganisationInfo.dummyForId(orgId)
		}

		if (!orgInfoCache.containsKey(orgId)) {
			OrganisationsApi api = UserServiceHelper.getOrganisationsApi(this,
					this.getSession().getToken());
			HaleConnectOrganisationInfo orgInfo;
			try {
				OrganisationInfo org = api.getOrganisation(orgId);
				orgInfo = HaleConnectOrganisationInfo.dummyForId(orgId)
			} catch (ApiException e) {
				if (e.code == HttpURLConnection.HTTP_NOT_FOUND) {
					orgInfo = HaleConnectOrganisationInfo.dummyForId(orgId)
				}
				else {
					throw new HaleConnectException(e.getMessage(), e);
				}
			}

			orgInfoCache.put(orgId, orgInfo);
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
			bucketDetail = ProjectStoreHelper.getBucketsApi(this, this.getSessionToken())
					.getBucketInfo(owner.getType().getJsonValue(), owner.getId(), projectId);
		} catch (com.haleconnect.api.projectstore.v1.ApiException e) {
			throw new HaleConnectException(e.getMessage(), e);
		}

		return processBucketDetail(bucketDetail);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#getProjects(String)
	 */
	@Override
	public List<HaleConnectProjectInfo> getProjects(String contextOrganisation)
	throws HaleConnectException {
		List<BucketDetail> bucketDetails;
		try {
			bucketDetails = ProjectStoreHelper.getBucketsApi(this, this.getSession().getToken())
					.getBuckets(contextOrganisation, true);
		} catch (com.haleconnect.api.projectstore.v1.ApiException e) {
			throw new HaleConnectException(e.getMessage(), e);
		}

		return processBucketDetails(bucketDetails);
	}

	@Override
	public ListenableFuture<List<HaleConnectProjectInfo>> getProjectsAsync(
			String contextOrganisation) throws HaleConnectException {
		final SettableFuture<List<HaleConnectProjectInfo>> future = SettableFuture.create();
		try {
			ProjectStoreHelper.getBucketsApi(this, this.getSession().getToken()).getBucketsAsync(
					contextOrganisation, true, new ApiCallback<List<BucketDetail>>() {

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

	@Override
	public String getSessionToken() {
		return session?.token;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#getUserInfo(java.lang.String)
	 */
	@Override
	public HaleConnectUserInfo getUserInfo(String userId) throws HaleConnectException {
		if (!this.isLoggedIn()) {
			return HaleConnectUserInfo.dummyForId(userId)
		}

		if (!userInfoCache.containsKey(userId)) {
			UsersApi api = UserServiceHelper.getUsersApi(this, this.getSession().getToken());

			HaleConnectUserInfo userInfo;
			try {
				UserInfo info = api.getProfile(userId);
				userInfo = new HaleConnectUserInfo(userId: info.getId(), screenName: info.getScreenName(), fullName: info.getFullName())
			} catch (ApiException e) {
				if (e.code == HttpURLConnection.HTTP_NOT_FOUND) {
					userInfo = HaleConnectUserInfo.dummyForId(userId)
				}
				else {
					throw new HaleConnectException(e.getMessage(), e)
				}
			}

			userInfoCache.put(userId, userInfo)
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
			// there may be public projects
			// throw new IllegalStateException("Not logged in.");
		}

		URI location = HaleConnectUrnBuilder.buildProjectUrn(owner, projectId);
		HaleConnectProjectInfo projectInfo = getProject(owner, projectId);
		if (projectInfo == null) {
			throw new HaleConnectException(
			MessageFormat.format("Project does not exist: {0}", location.toString()));
		}

		return new HaleConnectInputSupplier(location, this.getSessionToken(), this);
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
			else if (e.getCause() != null) {
				Throwable cause = e.getCause();
				throw new HaleConnectException(cause.getMessage(), cause);
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
	public ListenableFuture<Boolean> uploadProjectFileAsync(String projectId, Owner owner,
			File file, ProgressIndicator progress) throws HaleConnectException {

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
		} catch (com.haleconnect.api.projectstore.v1.ApiException e1) {
			throw new HaleConnectException(e1.getMessage(), e1, e1.getCode(),
			e1.getResponseHeaders());
		} catch (ExecutionException e2) {
			Throwable t = e2.getCause();
			if (t instanceof HaleConnectException) {
				throw (HaleConnectException) t;
			}
			else {
				throw new HaleConnectException(t.getMessage(), t);
			}
		} catch (InterruptedException e3) {
			throw new HaleConnectException(e3.getMessage(), e3);
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
		Long lastModified = bucket.getLastModified();
		if (bucket.getProperties() instanceof Map<?, ?>) {
			Map<Object, Object> properties = (Map<Object, Object>) bucket.getProperties();
			if (properties.containsKey("author")) {
				author = properties.get("author").toString();
			}
		}

		HaleConnectUserInfo user = null;
		String userId = bucket.getId().getUserId();
		HaleConnectOrganisationInfo org = null;
		String orgId = bucket.getId().getOrgId();

		try {
			if (!StringUtils.isEmpty(userId)) {
				user = this.getUserInfo(userId);
			} else if (!StringUtils.isEmpty(orgId)) {
				org = this.getOrganisationInfo(orgId);
			}
			else {
				throw new IllegalStateException("Bucket is missing both user and org id");
			}
		} catch (HaleConnectException e) {
			log.error(e.getMessage(), e);
			throw new IllegalStateException(e.getMessage(), e);
		}

		return new HaleConnectProjectInfo(bucket.getId().getTransformationproject(), user, org,
				bucket.getName(), author, lastModified);
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
	public boolean testProjectPermission(String permission, Owner owner, String projectId)
	throws HaleConnectException {
		PermissionsApi api = ProjectStoreHelper.getPermissionsApi(this,
				this.getSession().getToken());

		String combinedBucketId = MessageFormat.format("{0}.{1}.{2}",
				owner.getType().getJsonValue(), owner.getId(), projectId);
		try {
			api.testBucketPermission(permission, combinedBucketId);
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
				return !"false".equals(userPermission.toString());
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
								.anyMatch { cond -> "organisation".equals(cond.toString()) }
					}
				}
			}
		} catch (ApiException e) {
			throw new HaleConnectException(e.getMessage(), e);
		}

		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#setProjectName(java.lang.String,
	 *      eu.esdihumboldt.hale.io.haleconnect.Owner, java.lang.String)
	 */
	@Override
	public boolean setProjectName(String projectId, Owner owner, String name)
	throws HaleConnectException {

		// Build custom call because BucketsApi.setProjectName() is broken
		// (does not support plain text body)

		String path = MessageFormat.format("/buckets/{0}/{1}/{2}/name",
				owner.getType().getJsonValue(), owner.getId(), projectId);
		Feedback feedback = ProjectStoreHelper.executePlainTextCallWithFeedback("PUT", path, name,
				this, this.getSession().getToken());

		if (feedback.getError()) {
			log.error(MessageFormat.format(
					"Error setting name \"{0}\" for hale connect project {1}", name, projectId));
			return false;
		}

		return true;
	}

}
