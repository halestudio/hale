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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFileInfo;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.util.OutputStreamDecorator;

/**
 * Writes a project file
 * 
 * @author Simon Templer
 */
public class DefaultProjectWriter extends AbstractProjectWriter {

	/**
	 * Output stream for a ZIP entry
	 */
	private static class EntryOutputStream extends OutputStreamDecorator {

		private final ZipOutputStream zip;

		/**
		 * Create an output stream for a ZIP entry
		 * 
		 * @param zip the ZIP output stream
		 */
		public EntryOutputStream(ZipOutputStream zip) {
			super(zip);

			this.zip = zip;
		}

		/**
		 * @see OutputStreamDecorator#close()
		 */
		@Override
		public void close() throws IOException {
			// instead of closing the stream close the entry
			zip.closeEntry();
		}

	}

	/**
	 * The configuration parameter name for detailing if project files are to be
	 * placed outside the project archive
	 */
	public static final String PARAM_SEPARATE_FILES = "projectFiles.separate";

	/**
	 * If project files are to be placed outside the archive. Only has effect if
	 * {@link #archive} is <code>true</code>
	 */
	private boolean useSeparateFiles = false;

	/**
	 * If the project shall be saved to a ZIP archive
	 */
	private final boolean archive;

	/**
	 * @param archive if the project shall be saved to a ZIP archive
	 */
	public DefaultProjectWriter(boolean archive) {
		super();
		this.archive = archive;
	}

	/**
	 * @see AbstractExportProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();

		if (getProject() == null) {
			fail("The main project file has not been set");
		}
	}

	/**
	 * @see AbstractExportProvider#storeConfiguration(Map)
	 */
	@Override
	public void storeConfiguration(Map<String, Value> configuration) {
		// store if separate files are to be used
		configuration.put(PARAM_SEPARATE_FILES, Value.of(useSeparateFiles));

		super.storeConfiguration(configuration);
	}

	@Override
	public void setParameter(String name, Value value) {
		if (name.equals(PARAM_SEPARATE_FILES)) {
			setUseSeparateFiles(value.getAs(Boolean.class));
		}
		else {
			super.setParameter(name, value);
		}
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		boolean separateProjectFiles = !archive || isUseSeparateFiles();
		URI targetLocation = getTarget().getLocation();
		File targetFile;
		try {
			targetFile = new File(targetLocation);
		} catch (Exception e) {
			if (!archive) {
				// cannot save as XML if it's not a file
				reporter.error(new IOMessageImpl("Could not determine project file path.", e));
				reporter.setSuccess(false);
				return reporter;
			}

			targetFile = null;
			// if it's not a file, we must save the project files inside the zip
			// stream
			separateProjectFiles = false;
		}

		int entries = 1;
		if (getProjectFiles() != null) {
			entries += getProjectFiles().size();
		}
		progress.begin("Save project", entries);

		// clear project file information in project
		getProject().getProjectFiles().clear();

		// write additional project files if they are to be placed in separate
		// files
		if (separateProjectFiles && targetFile != null) {
			for (Entry<String, ProjectFile> entry : getProjectFiles().entrySet()) {
				String name = entry.getKey();

				// determine target file for project file
				File pfile = new File(targetFile.getParentFile(), targetFile.getName() + "." + name);

				// add project file information to project
				getProject().getProjectFiles().add(new ProjectFileInfo(name, pfile.toURI()));

				// write entry
				ProjectFile file = entry.getValue();
				OutputStream out = new BufferedOutputStream(new FileOutputStream(pfile));
				try {
					file.store(out);
				} catch (Exception e) {
					reporter.error(new IOMessageImpl("Error saving a project file.", e));
					reporter.setSuccess(false);
					return reporter;
				} finally {
					out.close();
				}

				progress.advance(1);
			}
		}

		if (archive) {
			// save to archive
			ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(getTarget()
					.getOutput()));
			try {
				// write main entry
				zip.putNextEntry(new ZipEntry(ProjectIO.PROJECT_FILE));
				try {
					Project.save(getProject(), new EntryOutputStream(zip));
				} catch (Exception e) {
					reporter.error(new IOMessageImpl("Could not save main project configuration.",
							e));
					reporter.setSuccess(false);
					return reporter;
				}
				zip.closeEntry();
				progress.advance(1);

				// write additional project files to zip stream
				if (getProjectFiles() != null && !separateProjectFiles) {
					for (Entry<String, ProjectFile> entry : getProjectFiles().entrySet()) {
						String name = entry.getKey();
						if (name.equalsIgnoreCase(ProjectIO.PROJECT_FILE)) {
							reporter.error(new IOMessageImpl(
									"Invalid file name {0}. File name may not match the name of the main project configuration.",
									null, -1, -1, name));
						}
						else {
							// write entry
							zip.putNextEntry(new ZipEntry(name));
							ProjectFile file = entry.getValue();
							try {
								file.store(new EntryOutputStream(zip));
							} catch (Exception e) {
								reporter.error(new IOMessageImpl("Error saving a project file.", e));
								reporter.setSuccess(false);
								return reporter;
							}
							zip.closeEntry();
						}

						progress.advance(1);
					}
				}
			} finally {
				zip.close();
			}
		}
		else {
			// save project file to XML
			OutputStream out = getTarget().getOutput();
			try {
				Project.save(getProject(), out);
			} catch (Exception e) {
				reporter.error(new IOMessageImpl("Could not save main project file.", e));
				reporter.setSuccess(false);
				return reporter;
			} finally {
				out.close();
			}

			progress.advance(1);
		}

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * @return the useSeparateFiles
	 */
	public boolean isUseSeparateFiles() {
		return useSeparateFiles;
	}

	/**
	 * @param useSeparateFiles the useSeparateFiles to set
	 */
	public void setUseSeparateFiles(boolean useSeparateFiles) {
		this.useSeparateFiles = useSeparateFiles;
	}

}
