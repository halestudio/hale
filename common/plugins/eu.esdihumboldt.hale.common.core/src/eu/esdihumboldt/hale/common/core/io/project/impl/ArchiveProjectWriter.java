/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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

import com.google.common.io.Files;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFileInfo;
import eu.esdihumboldt.hale.common.core.io.project.util.XMLSchemaUpdater;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;

/**
 * Save projects as zip
 * 
 * @author Patrick Lieb
 */
public class ArchiveProjectWriter extends AbstractExportProvider implements
		ProjectWriter {

	private Project project;

	private Map<String, ProjectFile> projectFiles;

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectWriter#setProjectFiles(java.util.Map)
	 */
	@Override
	public void setProjectFiles(Map<String, ProjectFile> projectFiles) {
		this.projectFiles = projectFiles;

	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectWriter#setProject(eu.esdihumboldt.hale.common.core.io.project.model.Project)
	 */
	@Override
	public void setProject(Project project) {
		this.project = project;

	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectWriter#getProject()
	 */
	@Override
	public Project getProject() {
		return project;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		XMLProjectWriter writer = new XMLProjectWriter();

		// 1. create temporary directory
		File tempDir = Files.createTempDir();
		File baseFile = new File(tempDir, "project.halex");
		LocatableOutputSupplier<FileOutputStream> out = new FileIOSupplier(
				baseFile);
		
		ZipOutputStream zip = new ZipOutputStream(getTarget().getOutput());
		updateResources(tempDir);
		
		// XXX set correct target of the project (in the halex-file)
		IOConfiguration config = project.getSaveConfiguration();
		config.getProviderConfiguration().remove(PARAM_TARGET);
		// why replace needed here???
		config.getProviderConfiguration().put(PARAM_TARGET, "file:/" + baseFile.getPath().replace("\\", "/"));
		project.setSaveConfiguration(config);
		
		writer.setTarget(out);
		writer.setProject(project);
		writer.setProjectFiles(projectFiles);
		IOReport report = writer.execute(progress, reporter);

		zipDirectory(tempDir, zip, "");

		zip.close();
		
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
	private void updateResources(File targetDirectory) {

//		byte[] readBuffer = new byte[2156];
//		int bytesIn = 0;
		
		XMLSchemaUpdater updater = new XMLSchemaUpdater();

		List<IOConfiguration> resources = project.getResources();
		int count = 1;
		for (IOConfiguration resource : resources) {
			Map<String, String> providerConfig = resource
					.getProviderConfiguration();
			Map<String, String> newProvConf = new HashMap<String, String>();
			for (String key : providerConfig.keySet()) {
				
				if (key.equals(ImportProvider.PARAM_SOURCE)) {
					URI path;
					try {
						path = new URI(providerConfig.get(key));
					} catch (URISyntaxException e1) {
						// TODO handle exception
						return;
					}
					if(!path.getScheme().equals("file")){
						// URI is not local
						continue;
					}
					File file = new File(path);

					try {
						File newDirectory = new File(targetDirectory.getAbsolutePath() + "/" +  "resource" + count);
						newDirectory.mkdir();
						File newFile = new File(newDirectory, file.getName());
						Files.copy(file, newFile);
						
						updater.execute(newFile, file);
					} catch (IOException e) {
						// TODO handle exception
					}
					// XXX better way?
					newProvConf.put(key, "file:/" + targetDirectory.getAbsolutePath().replace("\\", "/") + "/resource" + count + "/" + file.getName());
					count++;
				}
			}

			// update provider configuration
			for (String key : newProvConf.keySet()) {
				resource.getProviderConfiguration().remove(key);
				resource.getProviderConfiguration().put(key,
						newProvConf.get(key));
			}
		}
	}

	private void zipDirectory(File zipDir, ZipOutputStream zos, String parentFolder) {

		String[] dirList = zipDir.list();
		byte[] readBuffer = new byte[2156];
		int bytesIn = 0;

		for (int i = 0; i < dirList.length; i++) {

			File f = new File(zipDir, dirList[i]);
			if(f.isDirectory()){
				if(parentFolder.isEmpty())
					zipDirectory(f, zos, f.getName());
				else 
					zipDirectory(f, zos, parentFolder + "/" + f.getName());
			continue;
			}
			try {
				FileInputStream fis = new FileInputStream(f);
				ZipEntry anEntry;
				if(parentFolder.isEmpty())
					anEntry = new ZipEntry(f.getName());
				else
					anEntry = new ZipEntry(parentFolder + "/" + f.getName());
				zos.putNextEntry(anEntry);

				while ((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
				fis.close();
			} catch (IOException e) {
				// TODO handle exception
			}
		}
	}

	private boolean deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] fileList = directory.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory()) {
					deleteDirectory(fileList[i]);
				} else {
					try{
						fileList[i].delete();
					} catch (SecurityException e){
						// do nothing
					}
				}
			}
		}
		return (directory.delete());
	}

}
