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

package eu.esdihumboldt.hale.common.headless.scavenger;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.headless.report.ReportFile;

/**
 * Represents a project residing in a specific folder and its configuration. The
 * configuration is stored in a file in the project folder.
 * 
 * @param <C> the update context type
 * @author Simon Templer
 */
public class ProjectReference<C> {

	private static final ALogger log = ALoggerFactory.getLogger(ProjectReference.class);

	/**
	 * The name of the project configuration file in the project folder.
	 */
	public static final String CONFIG_FILE_NAME = "project.properties";

	/**
	 * The name of the log file in the project folder, containing the reports
	 * from loading the project.
	 */
	public static final String REPORT_FILE_NAME = "project-load.log";

	/**
	 * The configuration file.
	 */
	private final ProjectProperties config;

	/**
	 * The project information, if the project was loaded.
	 */
	private ProjectInfo projectInfo;

	/**
	 * The project folder
	 */
	private final File projectFolder;

	/**
	 * The project identifier.
	 */
	private final String projectId;

	/**
	 * @param projectFolder the project folder
	 * @param overrideProjectFile the name of the project file if it should
	 *            override the configuration, otherwise <code>null</code>
	 * @param projectId the project identifier
	 * @param defaultSettings the properties with default project settings, may
	 *            be <code>null</code>
	 * @throws IOException if accessing the project configuration file failed
	 */
	public ProjectReference(final File projectFolder, final String overrideProjectFile,
			final String projectId, Properties defaultSettings) throws IOException {
		this.projectFolder = projectFolder;
		this.projectId = projectId;
		config = new ProjectProperties(new File(projectFolder, CONFIG_FILE_NAME), defaultSettings);

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
	 * @param context the update context
	 */
	public void update(C context) {
		File projectFile = getProjectFile();
		if (projectFile == null || !projectFile.exists()) {
			// no project file

			// reset any runtime information
			projectInfo = null;

			onNotAvailable(context, projectId);
		}
		else {
			File reportFile = getLoadReportFile();

			if ((projectInfo == null || isForceClearReports()) && reportFile.exists()) {
				// delete old reports
				reportFile.delete();
			}

			// store reports in file
			ReportFile rf = new ReportFile(reportFile);

			// load project info if not yet done
			if (projectInfo == null) {
				projectInfo = loadProjectInfo(projectFile, rf);
			}

			if (projectInfo == null) {
				// can't load project
				onFailure(context, projectId);
			}
			else {
				onSuccess(context, projectId, projectFile, rf);
			}
		}
	}

	/**
	 * States if the report file should be deleted in {@link #update(Object)}
	 * even if the project info is already loaded.
	 * 
	 * @return if the report file should be deleted for an already loaded
	 *         project
	 */
	protected boolean isForceClearReports() {
		return false;
	}

	/**
	 * Called when the project was successfully loaded in
	 * {@link #update(Object)}.
	 * 
	 * @param context the update context
	 * @param projectId the project identifier
	 * @param projectFile the project file
	 * @param reportFile the report file to publish any additional reports to
	 */
	protected void onSuccess(C context, String projectId, File projectFile, ReportFile reportFile) {
		// do nothing
	}

	/**
	 * Called when the project failed to load in {@link #update(Object)}.
	 * 
	 * @param context the update context
	 * @param projectId the project identifier
	 */
	protected void onFailure(C context, String projectId) {
		// do nothing
	}

	/**
	 * Called when the project file is not available in {@link #update(Object)}.
	 * 
	 * @param context the update context
	 * @param projectId the project identifier
	 */
	protected void onNotAvailable(C context, String projectId) {
		// do nothing
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
	 * Set the internal project info.
	 * 
	 * @param projectInfo the project info to set
	 */
	protected void setProjectInfo(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	/**
	 * Get the file the reports for loading the project are stored in.
	 * 
	 * @return the report file
	 */
	public File getLoadReportFile() {
		return new File(projectFolder, REPORT_FILE_NAME);
	}

	/**
	 * Load project information.
	 * 
	 * @param projectFile the project file
	 * @param reportHandler the report handler
	 * @return the project info or <code>null</code> if the project file could
	 *         not be loaded
	 */
	protected ProjectInfo loadProjectInfo(File projectFile, ReportHandler reportHandler) {
		FileIOSupplier in = new FileIOSupplier(projectFile);
		ProjectReader reader = HaleIO
				.findIOProvider(ProjectReader.class, in, projectFile.getName());
		reader.setSource(in);
		try {
			IOReport report = reader.execute(null);
			reportHandler.publishReport(report);
		} catch (Exception e) {
			log.error(
					"Failed to load project information for project at "
							+ projectFile.getAbsolutePath(), e);
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
	 * @return the project settings
	 */
	protected ProjectProperties getConfig() {
		return config;
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

		if (candidates != null) {
			if (candidates.length == 1) {
				return candidates[0].getName();
			}

			// more than one candidate, do a more thorough check
			// TODO warn that there are multiple?
			for (File candidate : candidates) {
				FileIOSupplier supplier = new FileIOSupplier(candidate);
				// find content type against stream
				IContentType contentType = HaleIO.findContentType(ProjectReader.class, supplier,
						null);
				if (contentType != null) {
					return candidate.getName();
				}
			}
		}

		// none found? check in subdirectories
		File[] subdirs = projectDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() && !pathname.isHidden();
			}
		});
		if (subdirs != null) {
			for (File subdir : subdirs) {
				String name = findProjectFile(subdir);
				if (name != null) {
					return subdir.getName() + "/" + name;
				}
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

}
