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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.project.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFileInfo;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.util.InputStreamDecorator;
import eu.esdihumboldt.util.io.IOUtils;
import eu.esdihumboldt.util.io.PathUpdate;

/**
 * Reads a project file
 * 
 * @author Simon Templer
 */
public class DefaultProjectReader extends AbstractImportProvider implements ProjectReader {

	/**
	 * Input stream for a ZIP entry
	 */
	private static class EntryInputStream extends InputStreamDecorator {

		private final ZipInputStream zip;

		/**
		 * Create an input stream for a ZIP entry
		 * 
		 * @param zip the ZIP input stream
		 */
		public EntryInputStream(ZipInputStream zip) {
			super(zip);

			this.zip = zip;
		}

		/**
		 * @see InputStreamDecorator#close()
		 */
		@Override
		public void close() throws IOException {
			// instead of closing the stream, close the entry
			zip.closeEntry();
		}

	}

	private static final ALogger log = ALoggerFactory.getLogger(DefaultProjectReader.class);

	/**
	 * The additional project files, file names are mapped to project file
	 * objects
	 */
	private Map<String, ProjectFile> projectFiles;

	/**
	 * The main project file, <code>null</code> if not yet loaded
	 */
	private Project project;

	/**
	 * If the project shall be read from a ZIP archive
	 */
	private final boolean archive;

	/**
	 * @param archive if the project shall be read from a ZIP archive
	 */
	public DefaultProjectReader(boolean archive) {
		super();
		this.archive = archive;
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Load project", ProgressIndicator.UNKNOWN);

		project = null;

		InputStream in = getSource().getInput();
		if (archive) {
			// read from archive
			ZipInputStream zip = new ZipInputStream(new BufferedInputStream(in));
			try {
				ZipEntry entry;
				while ((entry = zip.getNextEntry()) != null) {
					String name = entry.getName();

					progress.setCurrentTask(MessageFormat.format("Load {0}", name));

					if (name.equals(ProjectIO.PROJECT_FILE)) {
						try {
							project = Project.load(new EntryInputStream(zip));
						} catch (Exception e) {
							// fail if main project file cannot be loaded
							throw new IOProviderConfigurationException(
									"Source is no valid project archive", e);
						}
					}
					else {
						ProjectFile file = projectFiles.get(name);

						if (file != null) {
							try {
								file.load(new EntryInputStream(zip));
							} catch (Exception e) {
								reporter.error(new IOMessageImpl(
										"Error while loading project file {0}, file will be reset.",
										e, -1, -1, name));
								// reset file
								file.reset();
							}
						}
					}
				}
			} finally {
				zip.close();
			}
		}
		else {
			// read from XML
			try {
				project = Project.load(in);
			} catch (Exception e) {
				// fail if main project file cannot be loaded
				throw new IOProviderConfigurationException("Source is no valid project file", e);
			} finally {
				in.close();
			}
		}

		PathUpdate update = new PathUpdate(URI.create(project.getSaveConfiguration()
				.getProviderConfiguration().get(ExportProvider.PARAM_TARGET)), getSource()
				.getLocation());

		// check if there are any external project files listed
		if (projectFiles != null) { // only if project files set at all
			for (ProjectFileInfo fileInfo : project.getProjectFiles()) {
				ProjectFile projectFile = projectFiles.get(fileInfo.getName());

				if (projectFile != null) {
					URI location = fileInfo.getLocation();
					if (!IOUtils.testStream(fileInfo.getLocation(), false))
						location = update.changePath(location);
					boolean fileSuccess = false;
					try {
						InputStream input = location.toURL().openStream();
						try {
							projectFile.load(input);
							fileSuccess = true;
						} catch (Exception e) {
							throw e; // hand down
						} finally {
							input.close();
						}
					} catch (Exception e) {
						log.debug("Loading project file failed", e);
					}

					if (!fileSuccess) {
						reporter.error(new IOMessageImpl(
								"Error while loading project file {0}, file will be reset.", null,
								-1, -1, fileInfo.getName()));
						projectFile.reset();
					}
				}
				else {
					reporter.info(new IOMessageImpl(
							"No handler for external project file {0} found.", null, -1, -1,
							fileInfo.getName()));
				}
			}
		}

		// clear project infos
		project.getProjectFiles().clear();

		progress.end();
		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * @see ProjectReader#setProjectFiles(Map)
	 */
	@Override
	public void setProjectFiles(Map<String, ProjectFile> projectFiles) {
		this.projectFiles = projectFiles;
	}

	/**
	 * @see ProjectReader#getProjectFiles()
	 */
	@Override
	public Map<String, ProjectFile> getProjectFiles() {
		return projectFiles;
	}

	/**
	 * @see ProjectReader#getProject()
	 */
	@Override
	public Project getProject() {
		return project;
	}

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		// TODO change?
		return false;
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return ProjectIO.PROJECT_TYPE_NAME;
	}

}
