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
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFileInfo;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.util.io.InputStreamDecorator;

/**
 * Reads a project file
 * 
 * @author Simon Templer
 */
public class DefaultProjectReader extends AbstractProjectReader {

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

//	private static final ALogger log = ALoggerFactory.getLogger(DefaultProjectReader.class);

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
							setProjectChecked(Project.load(new EntryInputStream(zip)), reporter);
						} catch (Exception e) {
							// fail if main project file cannot be loaded
							throw new IOProviderConfigurationException(
									"Source is no valid project archive", e);
						}
					}
					else {
						ProjectFile file = getProjectFiles().get(name);

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
				setProjectChecked(Project.load(in), reporter);
			} catch (Exception e) {
				// fail if main project file cannot be loaded
				throw new IOProviderConfigurationException("Source is no valid project file", e);
			} finally {
				in.close();
			}
		}

		URI oldProjectLocation;
		if (getProject().getSaveConfiguration() == null) {
			oldProjectLocation = getSource().getLocation();
		}
		else {
			oldProjectLocation = URI.create(getProject().getSaveConfiguration()
					.getProviderConfiguration().get(ExportProvider.PARAM_TARGET).as(String.class));
		}
		PathUpdate update = new PathUpdate(oldProjectLocation, getSource().getLocation());

		// check if there are any external project files listed
		if (getProjectFiles() != null) { // only if project files set at all
			for (ProjectFileInfo fileInfo : getProject().getProjectFiles()) {
				ProjectFile projectFile = getProjectFiles().get(fileInfo.getName());
				if (projectFile != null) {
					URI location = fileInfo.getLocation();
					location = update.findLocation(location, false,
							DefaultInputSupplier.SCHEME_LOCAL
									.equals(getSource().getLocation().getScheme()),
							false);
					if (location == null && getSource().getLocation() != null) {
						// not able to resolve location, try defaults instead

						// 1st try: appending file name to project location
						try {
							URI candidate = new URI(getSource().getLocation().toString() + "."
									+ fileInfo.getName());
							if (HaleIO.testStream(candidate, true)) {
								location = candidate;
							}
						} catch (URISyntaxException e) {
							// ignore
						}

						// 2nd try: file name next to project
						if (location != null) {
							try {
								String projectLoc = getSource().getLocation().toString();
								int index = projectLoc.lastIndexOf('/');
								if (index > 0) {
									URI candidate = new URI(projectLoc.substring(0, index + 1)
											+ fileInfo.getName());
									if (HaleIO.testStream(candidate, true)) {
										location = candidate;
									}
								}
							} catch (URISyntaxException e) {
								// ignore
							}
						}
					}
					boolean fileSuccess = false;
					if (location != null) {
						try {
							DefaultInputSupplier dis = new DefaultInputSupplier(location);
							try (InputStream input = dis.getInput()) {
								projectFile.load(input);
								fileSuccess = true;
							} catch (Exception e) {
								throw e; // hand down
							}
						} catch (Exception e) {
							reporter.error(new IOMessageImpl("Loading project file failed", e));
						}
					}

					if (!fileSuccess) {
						reporter.error(new IOMessageImpl(
								"Error while loading project file {0}, file will be reset.", null,
								-1, -1, fileInfo.getName()));
						projectFile.reset();
					}
				}
				else {
					reporter.info(
							new IOMessageImpl("No handler for external project file {0} found.",
									null, -1, -1, fileInfo.getName()));
				}
			}
		}

		// clear project infos
		/*
		 * XXX was there any particular reason why this was done? I suspect it
		 * was done so when saving the project this information is not saved
		 * again as-is, but on the basis of actual files written. However, this
		 * case is handled in the project writer already.
		 * 
		 * As this information is in fact necessary when trying to identify
		 * certain files like the alignment, clearing the list of project files
		 * was commented out.
		 */
//		getProject().getProjectFiles().clear();

		progress.end();
		reporter.setSuccess(true);
		return reporter;
	}
}
