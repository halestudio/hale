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
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.common.io.Files;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;

/**
 * Provides support for saving projects as zip
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
		// TODO Auto-generated method stub
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

		File tempDir = Files.createTempDir();
		
		File baseFile = new File(tempDir, "project.halex");
		LocatableOutputSupplier<FileOutputStream> out = new FileIOSupplier(baseFile);
		
		writer.setTarget(out);
		writer.setProject(project);
		writer.setProjectFiles(projectFiles);
		IOReport report = writer.execute(progress, reporter);

		setProject(writer.getProject());
		ZipOutputStream zip = new ZipOutputStream(getTarget().getOutput());
		baseFile.delete();
		zipDirectory(tempDir, zip);

		zip.close();

		tempDir.delete();
		
		return report;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		// TODO Auto-generated method stub
		return null;
	}

	private void zipDirectory(File zipDir, ZipOutputStream zos) {

		//
		String[] dirList = zipDir.list();
		byte[] readBuffer = new byte[2156];
		int bytesIn = 0;

		for (int i = 0; i < dirList.length; i++) {

			File f = new File(zipDir, dirList[i]);
			// only if there is a directory in zipDir
			// if (f.isDirectory()) {
			// zipDirectory(f, zos);
			// continue;
			// }
			try {
				FileInputStream fis = new FileInputStream(f);
				ZipEntry anEntry = new ZipEntry(f.getName());
				zos.putNextEntry(anEntry);

				while ((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
				fis.close();
			} catch (IOException e) {
				// handle exception
			}
		}

	}

}
