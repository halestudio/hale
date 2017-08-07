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

import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.google.common.util.concurrent.ListenableFuture;

import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.io.haleconnect.project.SharingOptions;

/**
 * Facade for the hale connect microservices
 * 
 * @author Florian Esser
 */
public interface HaleConnectService {

	/**
	 * Permission to read resources
	 */
	public static final String PERMISSION_READ = "read";

	/**
	 * Permission to edit resources
	 */
	public static final String PERMISSION_EDIT = "edit";

	/**
	 * Permission to create resources
	 */
	public static final String PERMISSION_CREATE = "create";

	/**
	 * Permission to delete resources
	 */
	public static final String PERMISSION_DELETE = "delete";

	/**
	 * The resource type for transformation projects
	 */
	public static final String RESOURCE_TRANSFORMATION_PROJECT = "TransformationProject";

	/**
	 * @return the {@link BasePathManager} for this service implementation
	 */
	BasePathManager getBasePathManager();

	/**
	 * Adds a listener
	 * 
	 * @param listener the listener to add
	 */
	void addListener(HaleConnectServiceListener listener);

	/**
	 * Removes a listener
	 * 
	 * @param listener the listener to remove
	 */
	void removeListener(HaleConnectServiceListener listener);

	/*
	 * User service methods
	 */

	/**
	 * Login to hale connect
	 * 
	 * @param username user name
	 * @param password password
	 * @return true, if the login attempt was successful, false otherwise
	 * @throws HaleConnectException thrown on any API errors that do not simply
	 *             indicate invalid credentials
	 */
	boolean login(String username, String password) throws HaleConnectException;

	/**
	 * Verify that the given credentials are valid
	 * 
	 * @param username user name
	 * @param password password
	 * @return true, if the credentials were accepted, false otherwise
	 * @throws HaleConnectException thrown on any API errors that do not simply
	 *             indicate invalid credentials
	 */
	boolean verifyCredentials(String username, String password) throws HaleConnectException;

	/**
	 * @return the currently active session or null
	 */
	HaleConnectSession getSession();

	/**
	 * Deletes all session information
	 */
	void clearSession();

	/**
	 * @return true if a login token was issued by hale connect
	 */
	boolean isLoggedIn();

	/**
	 * Get the profile of a user.
	 * 
	 * @param userId ID of the user
	 * @return user profile
	 * @throws HaleConnectException thrown on any API exception
	 */
	HaleConnectUserInfo getUserInfo(String userId) throws HaleConnectException;

	/**
	 * Get information about an organisation
	 * 
	 * @param orgId ID of the organisation
	 * @return org profile
	 * @throws HaleConnectException thrown on any API exception
	 */
	HaleConnectOrganisationInfo getOrganisationInfo(String orgId) throws HaleConnectException;

	/**
	 * Test if the currently logged on user has the specified permission
	 * 
	 * @param resourceType The resource type to test, usually one of the
	 *            <code>RESOURCE_</code> constants defined in
	 *            {@link HaleConnectService}.
	 * @param role The assumed role. This has to be either <code>user</code> (to
	 *            test if the user has a permission in his own right) or the ID
	 *            of an organisation (to test if a user has a permission on
	 *            behalf of an organisation).
	 * @param permission the permission to test, usually one of the
	 *            <code>PERMISSION_</code> constants defined in
	 *            {@link HaleConnectService}.
	 * @return true if the user has the given permission
	 * @throws HaleConnectException thrown on any API exception
	 */
	boolean testUserPermission(String resourceType, String role, String permission)
			throws HaleConnectException;

	/*
	 * Project store methods
	 */

	/**
	 * Get information on a project.
	 * 
	 * @param owner Owner of the project
	 * @param projectId ID of the project
	 * @return Information on the specified project or null if the project does
	 *         not exist
	 * @throws HaleConnectException thrown on any API exception
	 */
	HaleConnectProjectInfo getProject(Owner owner, String projectId) throws HaleConnectException;

	/**
	 * Get a list of available hale connect transformation projects
	 * 
	 * @return a list of available projects
	 * @throws HaleConnectException thrown on any API error
	 */
	List<HaleConnectProjectInfo> getProjects() throws HaleConnectException;

	/**
	 * Get a list of available hale connect transformation projects
	 *
	 * @return {@link ListenableFuture} of the result
	 * @throws HaleConnectException thrown on any API error
	 */
	ListenableFuture<List<HaleConnectProjectInfo>> getProjectsAsync() throws HaleConnectException;

	/**
	 * Load a transformation from hale connect
	 * 
	 * @param owner Project owner
	 * @param projectId Project ID
	 * @return A LocatableInputSupplier with an InputStream of the project data
	 * @throws HaleConnectException thrown on any API errors
	 */
	LocatableInputSupplier<InputStream> loadProject(Owner owner, String projectId)
			throws HaleConnectException;

	/**
	 * Create a new transformation project
	 * 
	 * @param name Project name
	 * @param author Project author
	 * @param owner Project owner
	 * @param versionControl whether to activate version control for the project
	 * @return the project ID
	 * @throws HaleConnectException thrown on any API exception
	 */
	String createProject(String name, String author, Owner owner, boolean versionControl)
			throws HaleConnectException;

	/**
	 * Upload a project file
	 * 
	 * @param projectId Transformation project ID
	 * @param owner Project owner
	 * @param file the file to upload
	 * @param progress a progress indicator
	 * @return true if the upload was successful
	 * @throws HaleConnectException thrown on any API exception
	 */
	boolean uploadProjectFile(String projectId, Owner owner, File file, ProgressIndicator progress)
			throws HaleConnectException;

	/**
	 * Upload a project asynchronously
	 * 
	 * @param projectId Transformation project ID
	 * @param owner Project owner
	 * @param file the file to upload
	 * @param progress a progress indicator
	 * @return a {@link ListenableFuture} whose value will be set to true upon
	 *         successful completion of the request or false when the request
	 *         fails
	 * @throws HaleConnectException thrown on any API exception
	 */
	ListenableFuture<Boolean> uploadProjectFileAsync(String projectId, Owner owner, File file,
			ProgressIndicator progress) throws HaleConnectException;

	/**
	 * Set the sharing options for a transformation project
	 * 
	 * @param projectId Transformation project ID
	 * @param owner Project owner
	 * @param options the options to set
	 * @return true if the options were successfully set
	 * @throws HaleConnectException thrown on any API exception
	 */
	boolean setProjectSharingOptions(String projectId, Owner owner, SharingOptions options)
			throws HaleConnectException;

	/**
	 * Set the project name
	 * 
	 * @param projectId Transformation project ID
	 * @param owner Project owner
	 * @param name Project name to set
	 * @return true if the name was successfully set
	 * @throws HaleConnectException thrown on any API exception
	 */
	boolean setProjectName(String projectId, Owner owner, String name) throws HaleConnectException;

	/**
	 * Test if the currently logged on user has the specified permission on a
	 * transformation project
	 * 
	 * @param permission the permission to test, usually one of the
	 *            <code>PERMISSION_</code> constants defined in
	 *            {@link HaleConnectService}.
	 * @param owner Owner of the transformation project
	 * @param projectId Transformation project ID
	 * @return true if the user has the given permission
	 * @throws HaleConnectException thrown on any API exception
	 */
	boolean testProjectPermission(String permission, Owner owner, String projectId)
			throws HaleConnectException;

}
