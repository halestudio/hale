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

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Load project from a zip-archive (created by {@link ArchiveProjectExport})
 * 
 * @author Patrick Lieb
 */
public class ArchiveProjectImport extends AbstractProjectReader {

	private static final ALogger log = ALoggerFactory.getLogger(ArchiveProjectImport.class);

	// The reader saves the temporary directory as 'source' to load all
	// resources, but sometimes you need the old originally source (the archive)
	private LocatableInputSupplier<? extends InputStream> oldSource;

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		XMLProjectReader reader = new XMLProjectReader();

		// user selected directory
		File importLocation = new File(getParameter("PARAM_IMPORT_LOCATION").toString());

		getZipFiles(getSource().getInput(), importLocation);

		// create the project file via XMLProjectReader
		File baseFile = new File(importLocation, "project.halex");
		LocatableInputSupplier<InputStream> source = new FileIOSupplier(baseFile);

		// save old save configuration
		oldSource = getSource();

		setSource(source);
		reader.setSource(source);
		reader.setProjectFiles(getProjectFiles());
		IOReport report = reader.execute(progress, reporter);
		setProject(reader.getProject());

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

	/**
	 * @return the originally source of the archive
	 */
	public LocatableInputSupplier<? extends InputStream> getOriginallySource() {
		return oldSource;
	}

}
