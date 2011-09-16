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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import eu.esdihumboldt.hale.common.core.io.ContentType;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.util.OutputStreamDecorator;

/**
 * Writes a project file
 * @author Simon Templer
 */
public class ZipProjectWriter extends AbstractExportProvider implements ProjectWriter {

	/**
	 * Output stream for a ZIP entry
	 */
	private static class EntryOutputStream extends OutputStreamDecorator {

		private final ZipOutputStream zip;

		/**
		 * Create an output stream for a ZIP entry 
		 * @param zip the ZIP output stream
		 */
		public EntryOutputStream(ZipOutputStream zip) {
			super(zip);
			
			this.zip = zip;
		}

		/**
		 * @see OutputStreamDecorator#close()
		 */
		@Override
		public void close() throws IOException {
			// instead of closing the stream close the entry
			zip.closeEntry();
		}

	}

	/**
	 * The additional project files, file names are mapped to project file objects
	 */
	private Map<String, ProjectFile> projectFiles;
	
	/**
	 * The main project file
	 */
	private Project project;

	/**
	 * @see AbstractExportProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();
		
		if (project == null) {
			fail("The main project file has not been set");
		}
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		int entries = 1;
		if (projectFiles != null) {
			entries += projectFiles.size();
		}
		progress.begin("Save project", entries);
		
		// update project with file information
		project.getFiles().clear();
		if (projectFiles != null) {
			for (Entry<String, ProjectFile> entry : projectFiles.entrySet()) {
				// remember project file classes
				project.getFiles().put(entry.getKey(), entry.getValue().getClass());
			}
		}
		
		ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(
				getTarget().getOutput()));
		try {
			// write main entry
			zip.putNextEntry(new ZipEntry(ProjectIO.PROJECT_FILE));
			try {
				Project.save(project, new EntryOutputStream(zip));
			} catch (Exception e) {
				reporter.error(new IOMessageImpl("Could not save main project configuration.", e));
				reporter.setSuccess(false);
				return reporter;
			}
			zip.closeEntry();
			progress.advance(1);
			
			// write additional project files
			for (Entry<String, ProjectFile> entry : projectFiles.entrySet()) {
				String name = entry.getKey();
				if (name.equalsIgnoreCase(ProjectIO.PROJECT_FILE)) {
					reporter.error(new IOMessageImpl(
							"Invalid file name {0}. File name may not match the name of the main project configuration.", 
							null, name));
				}
				else {
					// write entry
					zip.putNextEntry(new ZipEntry(name));
					ProjectFile file = entry.getValue();
					try {
						file.store(new EntryOutputStream(zip));
					} catch (Exception e) {
						reporter.error(new IOMessageImpl("Error saving a project file.", e));
						reporter.setSuccess(false);
						return reporter;
					}
					zip.closeEntry();
				}
				
				progress.advance(1);
			}
		} finally {
			zip.close();
		}
		
		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * @see ProjectWriter#setProjectFiles(Map)
	 */
	@Override
	public void setProjectFiles(Map<String, ProjectFile> projectFiles) {
		this.projectFiles = projectFiles;
	}

	/**
	 * @see ProjectWriter#setProject(Project)
	 */
	@Override
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * @see ProjectWriter#getProject()
	 */
	@Override
	public Project getProject() {
		return project;
	}

	/**
	 * @see AbstractIOProvider#getDefaultContentType()
	 */
	@Override
	protected ContentType getDefaultContentType() {
		return ContentType.getContentType(ProjectIO.PROJECT_CT_ID);
	}
	
	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

}
