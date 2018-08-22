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
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFileInfo;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.util.io.EntryOutputStream;
import eu.esdihumboldt.util.io.IOUtils;

/**
 * Writes a project file
 * 
 * @author Simon Templer
 */
public class DefaultProjectWriter extends AbstractProjectWriter {

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
			setUseSeparateFiles(value.as(Boolean.class));
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

		// clear project file information that may already be contained in the
		// project
		getProject().getProjectFiles().clear();

		// write additional project files if they are to be placed in separate
		// files
		if (separateProjectFiles && targetFile != null) {
			for (Entry<String, ProjectFile> entry : getProjectFiles().entrySet()) {
				String name = entry.getKey();

				// determine target file for project file
				String projectFileName = targetFile.getName() + "." + name;
				final File pfile = new File(targetFile.getParentFile(), projectFileName);
				// the following line is basically
				// URI.create(escape(projectFileName))
				URI relativeProjectFile = targetFile.getParentFile().toURI()
						.relativize(pfile.toURI());

				// add project file information to project
				getProject().getProjectFiles().add(new ProjectFileInfo(name, relativeProjectFile));

				// write entry
				ProjectFile file = entry.getValue();
				try {
					LocatableOutputSupplier<OutputStream> target = new LocatableOutputSupplier<OutputStream>() {

						@Override
						public OutputStream getOutput() throws IOException {
							return new BufferedOutputStream(new FileOutputStream(pfile));
						}

						@Override
						public URI getLocation() {
							return pfile.toURI();
						}
					};
					file.store(target);
				} catch (Exception e) {
					reporter.error(new IOMessageImpl("Error saving a project file.", e));
					reporter.setSuccess(false);
					return reporter;
				}
				progress.advance(1);
			}
		}

		updateRelativeResourcePaths(getProject().getResources(), getPreviousTarget(),
				targetLocation);

		if (archive) {
			// save to archive
			final ZipOutputStream zip = new ZipOutputStream(
					new BufferedOutputStream(getTarget().getOutput()));
			try {
				// write main entry
				zip.putNextEntry(new ZipEntry(ProjectIO.PROJECT_FILE));
				try {
					Project.save(getProject(), new EntryOutputStream(zip));
				} catch (Exception e) {
					reporter.error(
							new IOMessageImpl("Could not save main project configuration.", e));
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
								LocatableOutputSupplier<OutputStream> target = new LocatableOutputSupplier<OutputStream>() {

									private boolean first = true;

									@Override
									public OutputStream getOutput() throws IOException {
										if (first) {
											first = false;
											return new EntryOutputStream(zip);
										}
										throw new IllegalStateException(
												"Output stream only available once");
									}

									@Override
									public URI getLocation() {
										return getTarget().getLocation();
									}
								};
								file.store(target);
							} catch (Exception e) {
								reporter.error(
										new IOMessageImpl("Error saving a project file.", e));
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

	private void updateRelativeResourcePaths(Iterable<IOConfiguration> resources,
			URI previousTarget, URI newTarget) {
		// if the previous target is null, there cannot be relative paths
		if (previousTarget == null)
			return;
		for (IOConfiguration resource : resources) {
			Map<String, Value> providerConfig = resource.getProviderConfiguration();
			URI pathUri = URI
					.create(providerConfig.get(ImportProvider.PARAM_SOURCE).as(String.class));
			// update relative URIs
			if (!pathUri.isAbsolute()) {
				// resolve the resource's URI
				pathUri = previousTarget.resolve(pathUri);
				// try to get a relative path from the new project to the
				// resource
				URI relative = IOUtils.getRelativePath(pathUri, newTarget);
				providerConfig.put(ImportProvider.PARAM_SOURCE, Value.of(relative.toString()));
			}
		}
	}

}
