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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.SubtaskProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFileInfo;
import eu.esdihumboldt.hale.common.core.io.project.util.XMLAlignmentUpdater;
import eu.esdihumboldt.hale.common.core.io.project.util.XMLSchemaUpdater;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
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

	/**
	 * Parameter for including or excluding web resources
	 */
	public static final String INCLUDE_WEB_RESOURCES = "includeweb";

	/**
	 * Parameter for including or excluding data files
	 */
	public static final String EXLUDE_DATA_FILES = "excludedata";

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

		// false is correct if getParameter is null because false is default
		boolean includeWebresources = getParameter(INCLUDE_WEB_RESOURCES).as(Boolean.class, false);
		SubtaskProgressIndicator subtask = new SubtaskProgressIndicator(progress);

		// save old IO configurations
		List<IOConfiguration> oldResources = new ArrayList<IOConfiguration>();
		for (int i = 0; i < getProject().getResources().size(); i++)
			// clone all IO configurations to work on different objects
			oldResources.add(getProject().getResources().get(i).clone());
		IOConfiguration config = getProject().getSaveConfiguration();
		IOConfiguration oldSaveConfig = config.clone();

		// copy resources to the temp directory and update xml schemas
		updateResources(tempDir, includeWebresources, subtask, reporter);

		// update target save configuration of the project
		config.getProviderConfiguration().put(PARAM_TARGET, Value.of(baseFile.toURI().toString()));

		// write project file via XMLProjectWriter
		XMLProjectWriter writer = new XMLProjectWriter();
		writer.setTarget(out);
		writer.setProject(getProject());
		writer.setProjectFiles(getProjectFiles());
		IOReport report = writer.execute(progress, reporter);

		// now after the project with its project files is written, look for the
		// alignment file and update it
		for (ProjectFileInfo pfi : getProject().getProjectFiles())
			if (pfi.getName().equals("alignment.xml")) {
				URI newAlignment = pfi.getLocation();
				// URI probably is relative
				newAlignment = tempDir.toURI().resolve(newAlignment);
				// Use the project file as oldFile - assumes they are in the
				// same directory.
				XMLAlignmentUpdater.update(
						new File(newAlignment),
						URI.create(oldSaveConfig.getProviderConfiguration().get(PARAM_TARGET)
								.toString()), includeWebresources, reporter);
				break;
			}

		// put the complete temp directory into a zip file
		zipDirectory(tempDir, zip, "");
		zip.close();

		// delete the temp directory
		try {
			FileUtils.deleteDirectory(tempDir);
		} catch (IOException e) {
			log.debug("Can not delete directory " + tempDir.toString(), e);
		}

		// reset all IOConfigurations for further IO operations
		getProject().setSaveConfiguration(oldSaveConfig);
		List<IOConfiguration> resources = getProject().getResources();
		resources.clear();
		resources.addAll(oldResources);
		return report;
	}

	// update the resources and copy them into target directory
	private void updateResources(File targetDirectory, boolean includeWebResources,
			ProgressIndicator progress, IOReporter reporter) throws IOException {
		progress.begin("Copy resources", ProgressIndicator.UNKNOWN);
		try {
			List<IOConfiguration> resources = getProject().getResources();
			// every resource needs his own directory
			int count = 1;
			// true if excluded files should be skipped; false is default
			boolean excludeDataFiles = getParameter(EXLUDE_DATA_FILES).as(Boolean.class, false);
			Iterator<IOConfiguration> iter = resources.iterator();
			while (iter.hasNext()) {
				IOConfiguration resource = iter.next();
				// check if ActionId is equal to
				// eu.esdihumboldt.hale.common.instance.io.InstanceIO.ACTION_LOAD_SOURCE_DATA
				// import not possible due to cycle errors
				if (excludeDataFiles
						&& resource.getActionId().equals(
								"eu.esdihumboldt.hale.io.instance.read.source")) {
					// delete reference in project file
					iter.remove();
					continue;
				}

				Map<String, Value> providerConfig = resource.getProviderConfiguration();
				String path = providerConfig.get(ImportProvider.PARAM_SOURCE).toString();
				URI pathUri;
				try {
					pathUri = new URI(path);
				} catch (URISyntaxException e1) {
					reporter.error(new IOMessageImpl("Skipped resource because of invalid URI: "
							+ path, e1));
					continue;
				}
				String scheme = pathUri.getScheme();
				InputStream input = null;
				if (scheme != null) {
					if (includeWebResources || // web resources are OK
							!(scheme.equals("http") || scheme.equals("https"))
					// or not a web resource
					) {
						DefaultInputSupplier supplier = new DefaultInputSupplier(pathUri);
						input = supplier.getInput();
					}
					else {
						// web resource that should not be included this time
						continue;
					}
				}
				else {
					// now can't open that, can we?
					reporter.error(new IOMessageImpl(
							"Skipped resource because it cannot be loaded from "
									+ pathUri.toString(), null));
					continue;
				}

				progress.setCurrentTask("Copying resource at " + path);

				// every resource file is copied into an own resource
				// directory in the target directory
				File newDirectory = new File(targetDirectory, "resource" + count);
				try {
					newDirectory.mkdir();
				} catch (SecurityException e) {
					throw new IOException("Can not create directory " + newDirectory.toString(), e);
				}

				// the filename
				String name = path.toString().substring(path.lastIndexOf("/"), path.length());

				File newFile = new File(newDirectory, name);
				OutputStream output = new FileOutputStream(newFile);
				ByteStreams.copy(input, output);
				output.close();

				// update schema files

				// only xml schemas have to be updated
				Value contentType = providerConfig.get(ImportProvider.PARAM_CONTENT_TYPE);
				if (contentType != null && contentType.as(String.class).equals(XSD_CONTENT_TYPE)) {
					progress.setCurrentTask("Reorganizing XML schema at " + path);
					XMLSchemaUpdater.update(newFile, pathUri, includeWebResources, reporter);
				}

				providerConfig.put(ImportProvider.PARAM_SOURCE, Value.of(new File(new File(
						targetDirectory, "resource" + count), name).toURI().toString()));
				count++;
			}
		} finally {
			progress.end();
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
