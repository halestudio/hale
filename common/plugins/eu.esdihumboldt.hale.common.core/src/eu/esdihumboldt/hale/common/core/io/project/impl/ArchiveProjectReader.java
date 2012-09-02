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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.Files;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Load project from a zip-archive
 * 
 * @author Patrick Lieb
 */
public class ArchiveProjectReader extends AbstractImportProvider implements
		ProjectReader {

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
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectReader#setProjectFiles(java.util.Map)
	 */
	@Override
	public void setProjectFiles(Map<String, ProjectFile> projectFiles) {
		this.projectFiles = projectFiles;

	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectReader#getProjectFiles()
	 */
	@Override
	public Map<String, ProjectFile> getProjectFiles() {
		return projectFiles;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectReader#getProject()
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
		
		XMLProjectReader reader = new XMLProjectReader();

		File tempDir = Files.createTempDir();

		getZipFiles(getSource().getInput(), tempDir);
		
		File baseFile = new File(tempDir, "project.halex");
		LocatableInputSupplier<FileInputStream> source = new FileIOSupplier(
				baseFile);
		setSource(source);
		reader.setSource(source);
		IOReport report = reader.execute(progress, reporter);
		project = reader.getProject();

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

	private void getZipFiles(InputStream file, File destination) {
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
						File newF = new File(destination.getAbsolutePath() + "/"
								+ directory);
						try{
						newF.mkdirs();
						} catch (SecurityException e){
							// TODO: exception handling
						}
					}

					File nf = new File(destination.getAbsolutePath() + "/"
							+ entryName);
					fileoutputstream = new FileOutputStream(nf);

					while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
						fileoutputstream.write(buf, 0, n);

					fileoutputstream.close();
					zipinputstream.closeEntry();

				}
			} catch (FileNotFoundException e) {
				// TODO: exception handling
			} catch (SecurityException e){
				// TODO: exception handling
			} catch (IOException e) {
				// TODO: exception handling
			}

			try {
				zipinputstream.close();
			} catch (IOException e) {
				// TODO: exception handling
			}
	}

	private void deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] fileList = directory.listFiles();
			for (int i = fileList.length - 1; i > 0; i--) {
				if (fileList[i].isDirectory()) {
					deleteDirectory(fileList[i]);
				} else {
					fileList[i].deleteOnExit();
				}
			}
		}
	}
}
