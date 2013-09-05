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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.Files;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.util.LocationUpdater;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Load project from a zip-archive (created by {@link ArchiveProjectWriter})
 * 
 * @author Patrick Lieb
 */
public class ArchiveProjectReader extends AbstractProjectReader {

	private static final ALogger log = ALoggerFactory.getLogger(ArchiveProjectReader.class);

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		XMLProjectReader reader = new XMLProjectReader();

		// copy resources to a temporary directory
		File tempDir = Files.createTempDir();
		getZipFiles(getSource().getInput(), tempDir);

		// create the project file via XMLProjectReader
		File baseFile = new File(tempDir, "project.halex");
		LocatableInputSupplier<InputStream> tempSource = new FileIOSupplier(baseFile);

		// save old save configuration
		LocatableInputSupplier<? extends InputStream> oldSource = getSource();

		setSource(tempSource);
		reader.setSource(tempSource);
		reader.setProjectFiles(getProjectFiles());
		IOReport report = reader.execute(progress, reporter);
		Project readProject = reader.getProject();

		if (readProject != null) {
			/*
			 * Because the original source is only available here, update the
			 * project's resource paths here.
			 * 
			 * The only drawback is that the UILocationUpdater cannot be used.
			 */
			LocationUpdater updater = new LocationUpdater(readProject, tempSource.getLocation());
			// update resources
			// resources are made absolute (else they can't be found afterwards)
			updater.updateProject(false);
		}

		// set the real source
		setSource(oldSource);
		// set the read project
		setProject(readProject);

		// delete the temporary directory
		deleteDirectoryOnExit(tempDir);

		return report;
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

	private void deleteDirectoryOnExit(File directory) {
		if (directory.exists()) {
			directory.deleteOnExit();
			File[] files = directory.listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isDirectory()) {
						deleteDirectoryOnExit(f);
					}
					else {
						f.deleteOnExit();
					}
				}
			}
		}
	}

}
