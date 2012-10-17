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

package eu.esdihumboldt.hale.server.projects.impl.internal;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.headless.EnvironmentManager;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;
import eu.esdihumboldt.hale.common.headless.impl.ProjectTransformationEnvironment;
import eu.esdihumboldt.hale.server.projects.ProjectScavenger.Status;

/**
 * Represents a project residing in a specific folder and its configuration. The
 * configuration is stored in a file in the project folder.
 * 
 * @author Simon Templer
 */
public class ProjectHandler {

	/**
	 * The name of the project configuration file in the project folder.
	 */
	public static final String CONFIG_FILE_NAME = "project.properties";

	/**
	 * The configuration file.
	 */
	private final ProjectProperties config;

	/**
	 * The
	 */
	private TransformationEnvironment transformationEnvironment;

	private ProjectInfo projectInfo;

	private final File projectFolder;

	private Status status = Status.NOT_AVAILABLE;

	private final String projectId;

	/**
	 * @param projectFolder the project folder
	 * @param overrideProjectFile the name of the project file if it should
	 *            override the configuration, otherwise <code>null</code>
	 * @param projectId the project identifier
	 * @throws IOException if accessing the project configuration file failed
	 */
	public ProjectHandler(final File projectFolder, final String overrideProjectFile,
			final String projectId) throws IOException {
		this.projectFolder = projectFolder;
		this.projectId = projectId;
		config = new ProjectProperties(new File(projectFolder, CONFIG_FILE_NAME));

		// override project file name
		if (overrideProjectFile != null) {
			config.setProjectFileName(overrideProjectFile);
		}
	}

	/**
	 * Updates the project status from the configuration and if needed loads the
	 * project and transformation environment and adds or removes the
	 * transformation environment.
	 * 
	 * @param envManager the environment manager
	 */
	public void update(EnvironmentManager envManager) {
		File projectFile = getProjectFile();
		if (projectFile == null || !projectFile.exists()) {
			// no project file
			status = Status.NOT_AVAILABLE;
			// reset any runtime information
			projectInfo = null;
			transformationEnvironment = null;
			envManager.removeEnvironment(projectId);
		}
		else {
			// load project info if not yet done
			if (projectInfo == null) {
				projectInfo = loadProjectInfo(projectFile);
			}

			if (projectInfo == null) {
				// can't load project
				status = Status.BROKEN;
				transformationEnvironment = null;
				envManager.removeEnvironment(projectId);
			}
			else {
				if (config.isEnabled()) {
					// load transformation environment if not yet done
					if (transformationEnvironment == null) {
						try {
							transformationEnvironment = new ProjectTransformationEnvironment(
									projectId, new FileIOSupplier(projectFile));
							// TODO what about the reports?
						} catch (IOException e) {
							status = Status.BROKEN;
							envManager.removeEnvironment(projectId);
						}
					}
					else {
						// XXX somehow check if project was changed?
					}

					if (transformationEnvironment != null) {
						envManager.addEnvironment(transformationEnvironment);
						status = Status.ACTIVE;
					}
				}
				else {
					// clear transformation environment
					status = Status.INACTIVE;
					transformationEnvironment = null;
					envManager.removeEnvironment(projectId);
				}
			}
		}
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
	 * Get the project information if available.
	 * 
	 * @return the projectInfo
	 */
	public ProjectInfo getProjectInfo() {
		return projectInfo;
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
	 * Load project information.
	 * 
	 * @param projectFile the project file
	 * @return the project info or <code>null</code> if the project file could
	 *         not be loaded
	 */
	private ProjectInfo loadProjectInfo(File projectFile) {
		FileIOSupplier in = new FileIOSupplier(projectFile);
		ProjectReader reader = HaleIO
				.findIOProvider(ProjectReader.class, in, projectFile.getName());
		reader.setSource(in);
		try {
			reader.execute(null);
		} catch (Exception e) {
			return null;
		}
		return reader.getProject();
	}

	/**
	 * Get the project file is possible.
	 * 
	 * @return the project file or <code>null</code>
	 */
	protected File getProjectFile() {
		String projectFile = config.getProjectFileName();
		if (projectFile == null) {
			// determine the project file automatically
			projectFile = findProjectFile(projectFolder);
			config.setProjectFileName(projectFile);
		}
		if (projectFile == null) {
			return null;
		}
		return new File(projectFolder, projectFile);
	}

	/**
	 * Find a candidate for the project file to load.
	 * 
	 * @param projectDir the project directory
	 * @return the name of the project file candidate in that directory,
	 *         <code>null</code> if none was found
	 */
	protected String findProjectFile(File projectDir) {
		final Set<String> extensions = getSupportedExtensions();

		File[] candidates = projectDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				if (file.isFile() && !file.isHidden()) {
					String lowerName = file.getName().toLowerCase();
					for (String extension : extensions) {
						if (lowerName.endsWith(extension.toLowerCase())) {
							return true;
						}
					}
				}
				return false;
			}

		});

		if (candidates == null || candidates.length == 0) {
			return null;
		}

		if (candidates.length == 1) {
			return candidates[0].getName();
		}

		// more than one candidate, do a more thorough check
		// TODO warn that there are multiple?
		for (File candidate : candidates) {
			FileIOSupplier supplier = new FileIOSupplier(candidate);
			// find content type against stream
			IContentType contentType = HaleIO.findContentType(ProjectReader.class, supplier, null);
			if (contentType != null) {
				return candidate.getName();
			}
		}

		return null;
	}

	/**
	 * Get the supported file extensions for projects.
	 * 
	 * @return the set of file extensions (with leading dot)
	 */
	protected Set<String> getSupportedExtensions() {
		Collection<IOProviderDescriptor> providers = HaleIO
				.getProviderFactories(ProjectReader.class);

		// collect supported content types
		Set<String> supportedExtensions = new HashSet<String>();
		for (IOProviderDescriptor factory : providers) {
			for (IContentType type : factory.getSupportedTypes()) {
				String[] extensions = type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
				if (extensions != null) {
					for (String ext : extensions) {
						supportedExtensions.add('.' + ext);
					}
				}
			}
		}

		return supportedExtensions;
	}

	/**
	 * Activate the project.
	 * 
	 * @param environments the environment manager to publish the transformation
	 *            environment to
	 */
	public void activate(EnvironmentManager environments) {
		config.setEnabled(true);
		update(environments);
	}

	/**
	 * Deactivate the project.
	 * 
	 * @param environments the environment manager to remove the transformation
	 *            environment from if applicable
	 */
	public void deactivate(EnvironmentManager environments) {
		config.setEnabled(false);
		update(environments);
	}

}
