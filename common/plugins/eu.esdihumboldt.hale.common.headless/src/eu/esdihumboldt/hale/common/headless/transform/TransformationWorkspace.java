/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.headless.transform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.content.IContentType;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.common.headless.EnvironmentService;
import eu.esdihumboldt.hale.common.headless.HeadlessIO;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;
import eu.esdihumboldt.hale.common.headless.WorkspaceService;
import eu.esdihumboldt.hale.common.headless.report.ReportFile;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;

/**
 * A transformation workspace based on {@link WorkspaceService} and
 * {@link EnvironmentService}.
 * 
 * @author Simon Templer
 */
public class TransformationWorkspace {

	private static final ALogger log = ALoggerFactory.getLogger(TransformationWorkspace.class);

	/**
	 * Name of the report file placed in a workspace folder.
	 */
	private static final String WORKSPACE_REPORT_FILE = "reports.log";

	/**
	 * Name of the folder containing the source files in a transformation
	 * workspace.
	 */
	private static final String WORKSPACE_SOURCE_FOLDER = "source";

	/**
	 * Name of the folder containing the target files in a transformation
	 * workspace.
	 */
	private static final String WORKSPACE_TARGET_FOLDER = "target";

	/**
	 * Name of the workspace setting that holds information about the completion
	 * of the transformation.
	 */
	private static final String SETTING_TRANSFORMATION_SUCCESS = "transformationSuccess";

	private final WorkspaceService workspaces;

	private final String workspaceId;

	private final File workspace;

	private final File targetFolder;

	private final File sourceFolder;

	private final File reportFile;

	/**
	 * Create a new transformation workspace with a lease duration of one day.
	 * 
	 * @throws IllegalStateException if the {@link WorkspaceService} is not
	 *             available
	 */
	public TransformationWorkspace() {
		this(Duration.standardDays(1));
	}

	/**
	 * Create a new transformation workspace with a custom lease duration.
	 * 
	 * @param leaseDuration the lease duration of the workspace
	 * @throws IllegalStateException if the {@link WorkspaceService} is not
	 *             available
	 */
	public TransformationWorkspace(ReadableDuration leaseDuration) {
		this(null, leaseDuration);
	}

	/**
	 * Create a representation of an existing transformation workspace.
	 * 
	 * @param workspaceId the workspace identifier
	 * @throws IllegalStateException if the {@link WorkspaceService} is not
	 *             available or the workspace with the given identifier does not
	 *             exist
	 */
	public TransformationWorkspace(String workspaceId) {
		this(workspaceId, null);
	}

	/**
	 * Create a new workspace or use an existing one.
	 * 
	 * @param workspaceId the workspace identifier if this object should
	 *            represent an existing workspace, may be <code>null</code> if
	 *            leaseDuration is set.
	 * @param leaseDuration the lease duration of a new workspace to create, may
	 *            be <code>null</code> if workspaceId is set
	 * @throws IllegalStateException if the {@link WorkspaceService} is not
	 *             available or the workspace with the given identifier does not
	 *             exist
	 */
	protected TransformationWorkspace(final String workspaceId, ReadableDuration leaseDuration) {
		workspaces = HalePlatform.getService(WorkspaceService.class);

		if (workspaces == null) {
			throw new IllegalStateException("WorkspaceService not available through OSGi");
		}

		if (workspaceId == null) {
			this.workspaceId = workspaces.leaseWorkspace(leaseDuration);
		}
		else {
			this.workspaceId = workspaceId;
		}

		try {
			workspace = workspaces.getWorkspaceFolder(this.workspaceId);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Error accessing transformation workspace");
		}

		sourceFolder = new File(workspace, WORKSPACE_SOURCE_FOLDER);
		sourceFolder.mkdir();

		targetFolder = new File(workspace, WORKSPACE_TARGET_FOLDER);
		targetFolder.mkdir();

		// report file
		reportFile = new File(workspace, WORKSPACE_REPORT_FILE);
	}

	/**
	 * Transform the instances provided through the given instance readers and
	 * store the result in the {@link #getTargetFolder()}.
	 * 
	 * @param envId the environment ID
	 * @param sources the instance readers
	 * @param target the configuration of the target instance writer
	 * @return the future representing the successful completion of the
	 *         transformation (note that a successful completion doesn't
	 *         necessary mean there weren't any internal transformation errors)
	 * @throws Exception if launching the transformation fails
	 */
	public ListenableFuture<Boolean> transform(String envId, List<InstanceReader> sources,
			IOConfiguration target) throws Exception {
		EnvironmentService environments = HalePlatform.getService(EnvironmentService.class);
		if (environments == null) {
			throw new IllegalStateException("WorkspaceService not available through OSGi");
		}
		TransformationEnvironment env = environments.getEnvironment(envId);

		if (env == null) {
			throw new IllegalStateException(
					"Transformation environment for project " + envId + " not available.");
		}

		return transform(env, sources, target, null);
	}

	/**
	 * Transform the instances provided through the given instance readers and
	 * by default stores the result in the {@link #getTargetFolder()}.
	 * 
	 * @param env the transformation environment
	 * @param sources the instance readers
	 * @param target the configuration of the target instance writer
	 * @param customTarget the custom output supplier to use for the target,
	 *            <code>null</code> to use the default target in thet
	 *            {@link #getTargetFolder()}
	 * @return the future representing the successful completion of the
	 *         transformation (note that a successful completion doesn't
	 *         necessary mean there weren't any internal transformation errors)
	 * @throws Exception if launching the transformation fails
	 */
	public ListenableFuture<Boolean> transform(TransformationEnvironment env,
			List<InstanceReader> sources, IOConfiguration target,
			LocatableOutputSupplier<? extends OutputStream> customTarget) throws Exception {
		InstanceWriter writer = (InstanceWriter) HeadlessIO.loadProvider(target);
		// TODO determine content type if not set?

		// output file
		if (customTarget != null) {
			writer.setTarget(customTarget);
		}
		else {
			File out = new File(targetFolder,
					"result." + getFileExtension(writer.getContentType()));
			writer.setTarget(new FileIOSupplier(out));
		}

		ListenableFuture<Boolean> result = Transformation.transform(sources, writer, env,
				new ReportFile(reportFile), workspace.getName(),
				new DefaultTransformationSettings());

		Futures.addCallback(result, new FutureCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				try {
					setTransformationSuccess(result);
				} catch (IOException e) {
					log.error("Failed to set transformation success for workspace", e);
				}
			}

			@Override
			public void onFailure(Throwable t) {
				try {
					setTransformationSuccess(false);
				} catch (IOException e) {
					log.error("Failed to set transformation success for workspace", e);
				}
			}
		});

		return result;
	}

	/**
	 * Determines if a previously with
	 * {@link #transform(String, List, IOConfiguration)} started transformation
	 * process is finished. Regardless of the success or failure.
	 * 
	 * @return <code>true</code> if the transformation is finished,
	 *         <code>false</code> if the transformation is still running, no
	 *         transformation was started or the workspace no longer exists
	 */
	public boolean isTransformationFinished() {
		try {
			Map<String, String> settings = workspaces.getSettings(workspaceId);
			return settings.containsKey(SETTING_TRANSFORMATION_SUCCESS);
		} catch (IOException e) {
			// ignore
			return false;
		}
	}

	/**
	 * Determines if a previously with
	 * {@link #transform(String, List, IOConfiguration)} started transformation
	 * process was complete successfully. Note that a successful completion
	 * doesn't necessary mean there weren't any internal transformation errors.
	 * The {@link #getReportFile()} holds more detailed information.<br>
	 * <br>
	 * This method may only be called of the transformation is finished,
	 * otherwise an {@link IllegalStateException} will be thrown.
	 * 
	 * @return if the transformation was completed successfully
	 * @throws IllegalStateException if the transformation is not finished
	 * 
	 * @see #isTransformationFinished()
	 */
	public boolean isTransformationSuccessful() throws IllegalStateException {
		try {
			Map<String, String> settings = workspaces.getSettings(workspaceId);
			String success = settings.get(SETTING_TRANSFORMATION_SUCCESS);
			if (success == null) {
				throw new IllegalStateException("Transformation not finished");
			}
			return Boolean.parseBoolean(success);
		} catch (IOException e) {
			// ignore
			return false;
		}
	}

	/**
	 * Set if the transformation was successfully completed. Must be called when
	 * the transformation is finished. Also deletes the source folder in the
	 * workspace.
	 * 
	 * @param success if the transformation was completed successfully
	 * @throws FileNotFoundException if the workspace does not exist
	 * @throws IOException if the workspace configuration file cannot be read or
	 *             written
	 */
	protected void setTransformationSuccess(boolean success)
			throws FileNotFoundException, IOException {
		workspaces.set(workspaceId, SETTING_TRANSFORMATION_SUCCESS, String.valueOf(success));

		FileUtils.deleteDirectory(getSourceFolder());
	}

	/**
	 * Get the workspace settings.
	 * 
	 * @return the current workspace settings, changes to the map will not be
	 *         reflected in the settings
	 * @throws FileNotFoundException if the workspace does not exist
	 * @throws IOException if the workspace configuration file cannot be read
	 * 
	 * @see #set(String, String)
	 */
	public Map<String, String> getSettings() throws FileNotFoundException, IOException {
		return workspaces.getSettings(workspaceId);
	}

	/**
	 * Change a workspace setting.
	 * 
	 * @param setting the name of the setting
	 * @param value the value, <code>null</code> to remove the setting
	 * @throws FileNotFoundException if the workspace does not exist
	 * @throws IOException if the workspace configuration file cannot be read or
	 *             written
	 * 
	 * @see #getSettings()
	 */
	public void set(String setting, String value) throws FileNotFoundException, IOException {
		workspaces.set(workspaceId, setting, value);
	}

	/**
	 * Guess the file extension for a given I/O configuration.
	 * 
	 * @param config the I/O provider configuration
	 * @return the file extensions or a default, w/o leading dot
	 */
	public static String guessFileExtension(IOConfiguration config) {
		String id = config.getProviderConfiguration().get(IOProvider.PARAM_CONTENT_TYPE)
				.as(String.class);
		IContentType contentType = null;
		if (id != null) {
			contentType = HalePlatform.getContentTypeManager().getContentType(id);
		}
		return getFileExtension(contentType);
	}

	/**
	 * Get the default file extension for the given content type.
	 * 
	 * @param contentType the content type, may be <code>null</code>
	 * @return the file extension w/o leading dot
	 */
	private static String getFileExtension(IContentType contentType) {
		if (contentType != null) {

			String[] extensions = contentType.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
			if (extensions != null && extensions.length > 0) {
				return extensions[0];
			}
		}

		// fall-back
		return "out";
	}

	/**
	 * @return the workspace ID
	 */
	public String getId() {
		return workspaceId;
	}

	/**
	 * @return the workspace folder
	 */
	public File getWorkspace() {
		return workspace;
	}

	/**
	 * Get the target folder. This folder holds the transformation results after
	 * the transformation is finished and successful.
	 * 
	 * @return the target folder
	 * 
	 * @see #isTransformationFinished()
	 * @see #isTransformationSuccessful()
	 */
	public File getTargetFolder() {
		return targetFolder;
	}

	/**
	 * Get the source folder. Files placed in this folder will be deleted after
	 * the transformation has finished.
	 * 
	 * @return the source folder
	 */
	public File getSourceFolder() {
		return sourceFolder;
	}

	/**
	 * Get the report file. It holds information about the finished
	 * transformation.
	 * 
	 * @return the report file
	 * 
	 * @see #isTransformationFinished()
	 */
	public File getReportFile() {
		return reportFile;
	}

	/**
	 * Delete the workspace
	 */
	public void delete() {
		workspaces.deleteWorkspace(workspaceId);
	}

}
