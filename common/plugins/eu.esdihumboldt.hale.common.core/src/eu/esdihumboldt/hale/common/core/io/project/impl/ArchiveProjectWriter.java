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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.FileLocator;

import com.google.common.io.Files;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.util.XMLSchemaUpdater;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;

/**
 * Save projects (including all related resources) as an archive (zip)
 * 
 * @author Patrick Lieb
 */
public class ArchiveProjectWriter extends AbstractProjectWriter {

	private static final ALogger log = ALoggerFactory.getLogger(ArchiveProjectWriter.class);

	private static final String XSD_CONTENT_TYPE = "eu.esdihumboldt.hale.io.xsd";

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		// first all files are copied into a temporary directory
		File tempDir = Files.createTempDir();
		File baseFile = new File(tempDir, "project.halex");

		LocatableOutputSupplier<FileOutputStream> out = new FileIOSupplier(baseFile);
		ZipOutputStream zip = new ZipOutputStream(getTarget().getOutput());

		// copy resources to the temp directory and update xml schemas
		updateResources(tempDir);

		// update target save configuration of the project
		IOConfiguration config = getProject().getSaveConfiguration();
		config.getProviderConfiguration().remove(PARAM_TARGET);
		config.getProviderConfiguration().put(PARAM_TARGET, baseFile.toURI().toString());
		getProject().setSaveConfiguration(config);

		// write project file via XMLProjectWriter
		XMLProjectWriter writer = new XMLProjectWriter();
		writer.setTarget(out);
		writer.setProject(getProject());
		writer.setProjectFiles(getProjectFiles());
		IOReport report = writer.execute(progress, reporter);

		// put the complete temp directory into a zip file
		zipDirectory(tempDir, zip, "");
		zip.close();

		// delete the temp directory
		deleteDirectory(tempDir);

		return report;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return null;
	}

	// update the resources and copy them into target directory
	private void updateResources(File targetDirectory) throws IOException {

		List<IOConfiguration> resources = getProject().getResources();
		int count = 1;
		for (IOConfiguration resource : resources) {
			Map<String, String> providerConfig = resource.getProviderConfiguration();
			Map<String, String> newProvConf = new HashMap<String, String>();
			URI path;
			try {
				path = new URI(providerConfig.get(ImportProvider.PARAM_SOURCE));
			} catch (URISyntaxException e1) {
				log.debug("Path of resource is invalid", e1);
				continue;
			}
			String scheme = path.getScheme();
			if (scheme != null) {
				if (scheme.equals("bundleentry")) {
					try {
						path = FileLocator.toFileURL(path.toURL()).toURI();
					} catch (URISyntaxException e) {
						log.debug("Bundleentry is invalid", e);
						continue;
					}
				}
				else if (!scheme.equals("files")) {
					// URI represents no local file and can not be updated
					continue;
				}
			}

			// only xml schemas have to be updated
			String contentType = providerConfig.get(ImportProvider.PARAM_CONTENT_TYPE);
			if (contentType.equals(XSD_CONTENT_TYPE)) {
				File file = new File(path);

				// every resource file is copied into an own resource directory
				// in the target directory
				File newDirectory = new File(targetDirectory, "resource" + count);
				try {
					newDirectory.mkdir();
				} catch (SecurityException e) {
					throw new IOException("Can not create directory " + newDirectory.toString(), e);
				}
				File newFile = new File(newDirectory, file.getName());
				Files.copy(file, newFile);

				// the XMLSchemaUpdater manipulates the current schema and
				// copies the included and imported schemas to the directory
				XMLSchemaUpdater.update(newFile, file);
				newProvConf.put(ImportProvider.PARAM_SOURCE, new File(new File(targetDirectory,
						"resource" + count), file.getName()).toURI().toString());
				count++;
			}

			// update provider configuration
			for (String key : newProvConf.keySet()) {
				resource.getProviderConfiguration().remove(key);
				resource.getProviderConfiguration().put(key, newProvConf.get(key));
			}

		}
	}

	// zip all files (with subdirectories) to the ZipOutputStream
	private void zipDirectory(File zipDir, ZipOutputStream zos, String parentFolder)
			throws IOException {

		String[] dirList = zipDir.list();
		byte[] readBuffer = new byte[2156];
		int bytesIn = 0;

		for (int i = 0; i < dirList.length; i++) {

			File f = new File(zipDir, dirList[i]);
			if (f.isDirectory()) {
				if (parentFolder.isEmpty())
					zipDirectory(f, zos, f.getName());
				else
					zipDirectory(f, zos, parentFolder + "/" + f.getName());
				continue;
			}
			FileInputStream fis = new FileInputStream(f);
			ZipEntry anEntry;
			if (parentFolder.isEmpty())
				anEntry = new ZipEntry(f.getName());
			else
				anEntry = new ZipEntry(parentFolder + "/" + f.getName());
			zos.putNextEntry(anEntry);

			while ((bytesIn = fis.read(readBuffer)) != -1) {
				zos.write(readBuffer, 0, bytesIn);
			}
			fis.close();
		}
	}

	// delete the complete directory
	private void deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] fileList = directory.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory()) {
					deleteDirectory(fileList[i]);
				}
				else {
					try {
						fileList[i].delete();
					} catch (SecurityException e) {
						log.debug("Can not delete directories because of SecurityManager", e);
					}

				}
			}
		}
	}
}
