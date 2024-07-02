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
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;

/**
 * Load project from a zip-archive (created by {@link ArchiveProjectWriter})
 * 
 * @author Patrick Lieb
 */
public class ArchiveProjectImport extends AbstractImportProvider implements ImportProvider {

	private static final ALogger log = ALoggerFactory.getLogger(ArchiveProjectImport.class);

	/**
	 * The location of the extracted project file.
	 */
	private URI projectLocation;

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		// user selected directory
		File importLocation = new File(getParameter("PARAM_IMPORT_LOCATION").toString());
		getZipFiles(getSource().getInput(), importLocation);

		File baseFile = new File(importLocation, "project.halex");

		if (!baseFile.exists()) {
			/*
			 * If the archive was not saved as project archive but manually
			 * created, the file name may not be correct.
			 */

			// TODO detect project file location?!
		}

		projectLocation = baseFile.toURI();

		reporter.setSuccess(true);

		return reporter;
	}

	/**
	 * @return the location of the extracted project.
	 */
	public URI getProjectLocation() {
		return projectLocation;
	}

	// extract all files from the InputStream to the destination
	// InputStream must be based on a zip file
	// destination file has to be a directory
	private void getZipFiles(InputStream file, File destination) throws IOException {
		byte[] buf = new byte[1024];
		try (ZipInputStream zipInputStream = new ZipInputStream(file)) {
			ZipEntry zipEntry;

			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				String entryName = zipEntry.getName();

				// Normalize the entry name and ensure it doesn't point outside
				// the destination directory
				File newFile = new File(destination, entryName).getCanonicalFile();

				if (!newFile.getPath()
						.startsWith(destination.getCanonicalPath() + File.separator)) {
					throw new IOException(
							"Entry is outside of the target dir: " + zipEntry.getName());
				}

				if (zipEntry.isDirectory()) {
					if (!newFile.isDirectory() && !newFile.mkdirs()) {
						throw new IOException("Failed to create directory " + newFile);
					}
				}
				else {
					// Ensure parent directories are created
					File parent = newFile.getParentFile();
					if (!parent.isDirectory() && !parent.mkdirs()) {
						throw new IOException("Failed to create directory " + parent);
					}

					// Write the file content
					try (FileOutputStream fos = new FileOutputStream(newFile)) {
						int len;
						while ((len = zipInputStream.read(buf)) > 0) {
							fos.write(buf, 0, len);
						}
					}
				}

				zipInputStream.closeEntry();
			}
		} catch (FileNotFoundException e) {
			throw new IOException("Destination directory not found", e);
		}
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected String getDefaultTypeName() {
		return ProjectIO.PROJECT_TYPE_NAME;
	}
}
