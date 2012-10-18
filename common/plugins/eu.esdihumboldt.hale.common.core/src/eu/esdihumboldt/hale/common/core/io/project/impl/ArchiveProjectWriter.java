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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;

import com.google.common.io.ByteStreams;
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
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
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

	private static final String WEB_RESOURCES = "webresources";

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		// all files related to the project are copied into a temporary
		// directory first and the packed into a zip file

		// create temporary directory and project file
		File tempDir = Files.createTempDir();
		File baseFile = new File(tempDir, "project.halex");

		LocatableOutputSupplier<OutputStream> out = new FileIOSupplier(baseFile);
		ZipOutputStream zip = new ZipOutputStream(getTarget().getOutput());

		// false if getParameter is null is desired
		boolean webresources = Boolean.parseBoolean(getParameter(WEB_RESOURCES));
		// copy resources to the temp directory and update xml schemas
		updateResources(tempDir, webresources);

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
		try {
			FileUtils.deleteDirectory(tempDir);
		} catch (IOException e) {
			log.debug("Can not delete file", e);
		}

		return report;
	}

	// update the resources and copy them into target directory
	private void updateResources(File targetDirectory, boolean allResources) throws IOException {

		List<IOConfiguration> resources = getProject().getResources();
		// every resource needs his own directory
		int count = 1;
		for (IOConfiguration resource : resources) {
			Map<String, String> providerConfig = resource.getProviderConfiguration();
			Map<String, String> newProvConf = new HashMap<String, String>();
			String path = providerConfig.get(ImportProvider.PARAM_SOURCE);
			URI pathUri;
			try {
				pathUri = new URI(path);
			} catch (URISyntaxException e1) {
				log.debug("Path of resource is invalid", e1);
				continue;
			}
			String scheme = pathUri.getScheme();
			InputStream input = null;
			// if scheme is null it has to be a local file represented by a
			// relative path
			if (scheme != null) {
				if (allResources && (scheme.equals("http") || scheme.equals("https"))
						|| scheme.equals("resource")) {
					DefaultInputSupplier supplier = new DefaultInputSupplier(pathUri);
					input = supplier.getInput();
				}
				else if (scheme.equals("bundleentry")) {
					try {
						File in = new File(FileLocator.toFileURL(pathUri.toURL()).toURI());
						input = new FileInputStream(in);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
				else if (scheme.equals("file")) {
					input = new FileInputStream(new File(pathUri));
				}
				else
					continue;
			}
			else
				input = new FileInputStream(new File(pathUri));

			// only xml schemas have to be updated
			String contentType = providerConfig.get(ImportProvider.PARAM_CONTENT_TYPE);
			if (contentType.equals(XSD_CONTENT_TYPE)) {

				// every resource file is copied into an own resource directory
				// in the target directory
				File newDirectory = new File(targetDirectory, "resource" + count);
				try {
					newDirectory.mkdir();
				} catch (SecurityException e) {
					throw new IOException("Can not create directory " + newDirectory.toString(), e);
				}
				// get name of the file
				String name = path.toString().substring(path.lastIndexOf("/"), path.length());
				File newFile = new File(newDirectory, name);
				OutputStream output = new FileOutputStream(newFile);
				ByteStreams.copy(input, output);

				// the XMLSchemaUpdater manipulates the current schema and
				// copies the included and imported schemas to the directory
				XMLSchemaUpdater.update(newFile, pathUri, allResources);
				newProvConf.put(ImportProvider.PARAM_SOURCE, new File(new File(targetDirectory,
						"resource" + count), name).toURI().toString());
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
}
