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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Load project from a zip-archive (created by {@link ArchiveProjectWriter})
 * 
 * @author Patrick Lieb
 */
public class ArchiveProjectImport extends AbstractImportProvider implements ImportProvider {

	private static final ALogger log = ALoggerFactory.getLogger(ArchiveProjectImport.class);

	/**
	 * The additional project files, file names are mapped to project file
	 * objects
	 */
	private Map<String, ProjectFile> projectFiles;

	/**
	 * The main project file, <code>null</code> if not yet loaded
	 */
	private Project project = null;

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		// user selected directory
		File importLocation = new File(getParameter("PARAM_IMPORT_LOCATION").toString());
		getZipFiles(getSource().getInput(), importLocation);

		File baseFile = new File(importLocation, "project.halex");
		LocatableInputSupplier<InputStream> source = new FileIOSupplier(baseFile);

		setSource(source);
		reporter.setSuccess(true);

		return reporter;
	}

	// extract all files from the InputStream to the destination
	// InputStream must be based on a zip file
	// destination file has to be a directory
	private void getZipFiles(InputStream file, File destination) throws IOException {
		byte[] buf = new byte[1024];
		ZipInputStream zipinputstream = null;
		ZipEntry zipentry;
		zipinputstream = new ZipInputStream(file);

		try {
			while ((zipentry = zipinputstream.getNextEntry()) != null) {
				String entryName = zipentry.getName();
				int n;
				FileOutputStream fileoutputstream;
				File newFile = new File(entryName);
				String directory = newFile.getParent();

				if (directory != null) {
					File newF = new File(destination, directory);
					try {
						newF.mkdirs();
					} catch (SecurityException e) {
						log.debug("Can not create directories because of SecurityManager", e);
					}
				}

				File nf = new File(destination, entryName);
				fileoutputstream = new FileOutputStream(nf);

				while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
					fileoutputstream.write(buf, 0, n);

				fileoutputstream.close();
				zipinputstream.closeEntry();

			}
		} catch (FileNotFoundException e) {
			throw new IOException("Destination directory not found", e);
		}

		zipinputstream.close();
	}

	/**
	 * @see ProjectReader#setProjectFiles(Map)
	 */
	public void setProjectFiles(Map<String, ProjectFile> projectFiles) {
		this.projectFiles = projectFiles;
	}

	/**
	 * @see ProjectReader#getProjectFiles()
	 */
	public Map<String, ProjectFile> getProjectFiles() {
		return projectFiles;
	}

	/**
	 * @see ProjectReader#getProject()
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project set the current project
	 */
	public void setProject(Project project) {
		this.project = project;
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
