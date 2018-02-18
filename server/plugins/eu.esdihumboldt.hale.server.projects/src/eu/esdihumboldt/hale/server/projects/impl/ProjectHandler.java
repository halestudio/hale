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

package eu.esdihumboldt.hale.server.projects.impl;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Reporter;
import eu.esdihumboldt.hale.common.core.report.impl.DefaultReporter;
import eu.esdihumboldt.hale.common.core.report.impl.MessageImpl;
import eu.esdihumboldt.hale.common.headless.EnvironmentManager;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;
import eu.esdihumboldt.hale.common.headless.impl.ProjectTransformationEnvironment;
import eu.esdihumboldt.hale.common.headless.report.ReportFile;
import eu.esdihumboldt.hale.common.headless.scavenger.ProjectReference;
import eu.esdihumboldt.hale.server.projects.ProjectScavenger.Status;

/**
 * Represents a project residing in a specific folder and its configuration. The
 * configuration is stored in a file in the project folder.
 * 
 * @author Simon Templer
 */
public class ProjectHandler extends ProjectReference<EnvironmentManager> {

	private static final ALogger log = ALoggerFactory.getLogger(ProjectHandler.class);

	/**
	 * Name of the property specifying if a project is enabled
	 */
	public static final String PROPERTY_ENABLED = "enabled";

	/**
	 * Default properties
	 */
	private static final Properties DEFAULT_PROPERTIES = new Properties();

	static {
		// configure properties defaults
		DEFAULT_PROPERTIES.setProperty(PROPERTY_ENABLED, "true");
	}

	/**
	 * The transformation environment, if the project is active.
	 */
	private TransformationEnvironment transformationEnvironment;

	/**
	 * The current project status.
	 */
	private Status status = Status.NOT_AVAILABLE;

	/**
	 * @param projectFolder the project folder
	 * @param overrideProjectFile the name of the project file if it should
	 *            override the configuration, otherwise <code>null</code>
	 * @param projectId the project identifier
	 * @throws IOException if accessing the project configuration file failed
	 */
	public ProjectHandler(final File projectFolder, final String overrideProjectFile,
			final String projectId) throws IOException {
		super(projectFolder, overrideProjectFile, projectId, DEFAULT_PROPERTIES);
	}

	@Override
	protected boolean isForceClearReports() {
		/*
		 * Force deleting existing project load reports if the transformation
		 * environment has to be created still.
		 */
		return transformationEnvironment == null && isEnabled();
	}

	/**
	 * Specifies if the project is enabled.
	 * 
	 * @return if the project is enabled in the configuration
	 */
	protected boolean isEnabled() {
		return Boolean.parseBoolean(getConfig().getPropertyQuiet(PROPERTY_ENABLED));
	}

	/**
	 * Set if the project is enabled.
	 * 
	 * @param enabled if the project is enabled
	 */
	protected void setEnabled(boolean enabled) {
		getConfig().setPropertyQuiet(PROPERTY_ENABLED, String.valueOf(enabled));
	}

	@Override
	protected void onSuccess(EnvironmentManager context, String projectId, File projectFile,
			Project project, ReportFile reportFile) {
		super.onSuccess(context, projectId, projectFile, project, reportFile);

		if (isEnabled()) {
			// load transformation environment if not yet done
			if (transformationEnvironment == null) {
				try {
					transformationEnvironment = new ProjectTransformationEnvironment(projectId,
							new FileIOSupplier(projectFile), reportFile);
					// check alignment
					if (transformationEnvironment.getAlignment() == null) {
						throw new IllegalStateException("Alignment missing or failed to load");
					}
					if (transformationEnvironment.getAlignment().getActiveTypeCells().isEmpty()) {
						throw new IllegalStateException(
								"Alignment contains no active type relations");
					}
				} catch (Exception e) {
					log.error("Could not load transformation environment for project " + projectId,
							e);
					status = Status.BROKEN;
					transformationEnvironment = null;
					context.removeEnvironment(projectId);

					// log the exception as report
					Reporter<Message> report = new DefaultReporter<Message>(
							"Load project transformation environment",
							ProjectIO.ACTION_LOAD_PROJECT, Message.class, false);
					report.error(new MessageImpl(e.getMessage(), e));
					reportFile.publishReport(report);
				}
			}
			else {
				// XXX somehow check if project was changed?
			}

			if (transformationEnvironment != null) {
				context.addEnvironment(transformationEnvironment);
				status = Status.ACTIVE;
			}
		}
		else {
			// clear transformation environment
			status = Status.INACTIVE;
			transformationEnvironment = null;
			context.removeEnvironment(projectId);
		}
	}

	@Override
	protected void onFailure(EnvironmentManager context, String projectId) {
		super.onFailure(context, projectId);

		// could not load project

		// update status
		status = Status.BROKEN;

		// reset any runtime information
		transformationEnvironment = null;

		// remove from environment manager
		context.removeEnvironment(projectId);
	}

	@Override
	protected void onNotAvailable(EnvironmentManager context, String projectId) {
		super.onNotAvailable(context, projectId);

		// update status
		status = Status.NOT_AVAILABLE;

		// reset any runtime information
		transformationEnvironment = null;

		// remove from environment manager
		context.removeEnvironment(projectId);
	}

	/**
	 * Get the transformation environment if available.
	 * 
	 * @return the transformationEnvironment
	 */
	public TransformationEnvironment getTransformationEnvironment() {
		return transformationEnvironment;
	}

	/**
	 * Get the project status.
	 * 
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Activate the project.
	 * 
	 * @param environments the environment manager to publish the transformation
	 *            environment to
	 */
	public void activate(EnvironmentManager environments) {
		setEnabled(true);
		update(environments);
	}

	/**
	 * Deactivate the project.
	 * 
	 * @param environments the environment manager to remove the transformation
	 *            environment from if applicable
	 */
	public void deactivate(EnvironmentManager environments) {
		setEnabled(false);
		update(environments);
	}

}
