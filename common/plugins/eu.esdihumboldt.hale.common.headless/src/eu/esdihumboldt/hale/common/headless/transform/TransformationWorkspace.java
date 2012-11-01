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
import java.util.List;

import org.eclipse.core.runtime.content.IContentType;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
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
		workspaces = OsgiUtils.getService(WorkspaceService.class);

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
	 * @throws Exception if launching the transformation fails
	 */
	public void transform(String envId, List<InstanceReader> sources, IOConfiguration target)
			throws Exception {
		EnvironmentService environments = OsgiUtils.getService(EnvironmentService.class);
		if (environments == null) {
			throw new IllegalStateException("WorkspaceService not available through OSGi");
		}
		TransformationEnvironment env = environments.getEnvironment(envId);

		if (env == null) {
			throw new IllegalStateException("Transformation environment for project " + envId
					+ " not available.");
		}

		InstanceWriter writer = (InstanceWriter) HeadlessIO.loadProvider(target);
		// TODO determine content type if not set?

		// output file
		File out = new File(targetFolder, "result." + getFileExtension(writer.getContentType()));
		writer.setTarget(new FileIOSupplier(out));

		Transformation.transform(sources, writer, env, new ReportFile(reportFile),
				workspace.getName());

		// TODO transformation call-back?
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
	 * @return the workspaceId
	 */
	public String getId() {
		return workspaceId;
	}

	/**
	 * @return the workspace
	 */
	public File getWorkspace() {
		return workspace;
	}

	/**
	 * @return the targetFolder
	 */
	public File getTargetFolder() {
		return targetFolder;
	}

	/**
	 * @return the sourceFolder
	 */
	public File getSourceFolder() {
		return sourceFolder;
	}

	/**
	 * @return the reportFile
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
