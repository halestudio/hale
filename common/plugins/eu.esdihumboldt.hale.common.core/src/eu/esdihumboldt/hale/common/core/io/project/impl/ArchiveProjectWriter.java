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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

import com.google.common.io.Files;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.ResourceAdvisor;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.ResourceAdvisorExtension;
import eu.esdihumboldt.hale.common.core.io.impl.SubtaskProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFileInfo;
import eu.esdihumboldt.hale.common.core.io.project.util.XMLAlignmentUpdater;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;

/**
 * Save projects (including all related resources) as an archive (zip)
 * 
 * @author Patrick Lieb
 */
public class ArchiveProjectWriter extends AbstractProjectWriter {

	private static final ALogger log = ALoggerFactory.getLogger(ArchiveProjectWriter.class);

	/**
	 * The provider ID as registered in the extension point.
	 */
	public static final String ID = "eu.esdihumboldt.hale.io.project.hale25.zip.writer";

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
		// directory first and then packed into a zip file

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
		for (int i = 0; i < getProject().getResources().size(); i++) {
			// clone all IO configurations to work on different objects
			oldResources.add(getProject().getResources().get(i).clone());
		}
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
		ProjectFileInfo newAlignmentInfo = getAlignmentFile(getProject());
		if (newAlignmentInfo != null) {
			URI newAlignment = tempDir.toURI().resolve(newAlignmentInfo.getLocation());
			XMLAlignmentUpdater.update(new File(newAlignment), newAlignment, includeWebresources,
					reporter);
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

	private ProjectFileInfo getAlignmentFile(Project project) {
		for (ProjectFileInfo pfi : project.getProjectFiles())
			if (pfi.getName().equals("alignment.xml")) {
				return pfi;
			}
		return null;
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

			// resource locations mapped to new resource path
			Map<URI, String> handledResources = new HashMap<>();

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

				// get resource path
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
				if (!pathUri.isAbsolute())
					pathUri = getPreviousTarget().resolve(pathUri);

				// check if path was already handled
				if (handledResources.containsKey(pathUri)) {
					providerConfig.put(ImportProvider.PARAM_SOURCE,
							Value.of(handledResources.get(pathUri)));
					// skip copying the resource
					continue;
				}

				String scheme = pathUri.getScheme();
				LocatableInputSupplier<? extends InputStream> input = null;
				if (scheme != null) {
					if (includeWebResources || // web resources are OK
							!(scheme.equals("http") || scheme.equals("https"))
					// or not a web resource
					) {
						input = new DefaultInputSupplier(pathUri);
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
				String resourceFolder = "resource" + count;
				File newDirectory = new File(targetDirectory, resourceFolder);
				try {
					newDirectory.mkdir();
				} catch (SecurityException e) {
					throw new IOException("Can not create directory " + newDirectory.toString(), e);
				}

				// the filename
				String name = path.toString().substring(path.lastIndexOf("/") + 1, path.length());

				File newFile = new File(newDirectory, name);
				Path target = newFile.toPath();

				// retrieve the resource advisor
				Value ct = providerConfig.get(ImportProvider.PARAM_CONTENT_TYPE);
				IContentType contentType = null;
				if (ct != null) {
					contentType = Platform.getContentTypeManager().getContentType(
							ct.as(String.class));
				}
				ResourceAdvisor ra = ResourceAdvisorExtension.getInstance().getAdvisor(contentType);

				// copy the resource
				progress.setCurrentTask("Copying resource at " + path);
				ra.copyResource(input, target, contentType, includeWebResources, reporter);

				// store new path for resource
				String newPath = resourceFolder + "/" + name;
				handledResources.put(pathUri, newPath);
				// update the provider configuration
				providerConfig.put(ImportProvider.PARAM_SOURCE, Value.of(newPath));
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
