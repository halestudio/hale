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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFileInfo;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.util.OutputStreamDecorator;

/**
 * Writes a project file
 * @author Simon Templer
 */
public class DefaultProjectWriter extends AbstractExportProvider implements ProjectWriter {

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
	 * The configuration parameter name for detailing if project files are to 
	 * be placed outside the project archive
	 */
	public static final String PARAM_SEPARATE_FILES = "projectFiles.separate";

	/**
	 * The additional project files, file names are mapped to project file objects
	 */
	private Map<String, ProjectFile> projectFiles;
	
	/**
	 * The main project file
	 */
	private Project project;
	
	/**
	 * If project files are to be placed outside the archive. Only has effect
	 * if {@link #archive} is <code>true</code>
	 */
	private boolean useSeparateFiles = false;
	
	/**
	 * If the project shall be saved to a ZIP archive
	 */
	private final boolean archive;

	/**
	 * @param archive if the project shall be saved to a ZIP archive
	 */
	public DefaultProjectWriter(boolean archive) {
		super();
		this.archive = archive;
	}

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
	 * @see AbstractExportProvider#storeConfiguration(Map)
	 */
	@Override
	public void storeConfiguration(Map<String, String> configuration) {
		// store if separate files are to be used
		configuration.put(PARAM_SEPARATE_FILES, String.valueOf(useSeparateFiles));
		
		super.storeConfiguration(configuration);
	}

	/**
	 * @see AbstractExportProvider#setParameter(String, String)
	 */
	@Override
	public void setParameter(String name, String value) {
		if (name.equals(PARAM_SEPARATE_FILES)) {
			setUseSeparateFiles(Boolean.valueOf(value));
		}
		else {
			super.setParameter(name, value);
		}
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		boolean separateProjectFiles = !archive || isUseSeparateFiles();
		URI targetLocation = getTarget().getLocation();
		File targetFile;
		try {
			targetFile = new File(targetLocation);
		} catch (Exception e) {
			if (!archive) {
				// cannot save as XML if it's not a file
				reporter.error(new IOMessageImpl("Could not determine project file path.", e));
				reporter.setSuccess(false);
				return reporter;
			}
			
			targetFile = null;
			// if it's not a file, we must save the project files inside the zip stream
			separateProjectFiles = false;
		}
		
		int entries = 1;
		if (projectFiles != null) {
			entries += projectFiles.size();
		}
		progress.begin("Save project", entries);
		
		// clear project file information in project
		project.getProjectFiles().clear();
		
		// write additional project files if they are to be placed in separate files
		if (separateProjectFiles && targetFile != null) {
			for (Entry<String, ProjectFile> entry : projectFiles.entrySet()) {
				String name = entry.getKey();
				
				// determine target file for project file
				File pfile = new File(targetFile.getParentFile(), 
						targetFile.getName() + "." + name);
				
				// add project file information to project
				project.getProjectFiles().add(new ProjectFileInfo(name, pfile.toURI()));
				
				// write entry
				ProjectFile file = entry.getValue();
				FileOutputStream out = new FileOutputStream(pfile);
				try {
					file.store(out);
				} catch (Exception e) {
					reporter.error(new IOMessageImpl("Error saving a project file.", e));
					reporter.setSuccess(false);
					return reporter;
				} finally {
					out.close();
				}
				
				progress.advance(1);
			}
		}
		
		if (archive) {
			// save to archive
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
				
				// write additional project files to zip stream
				if (projectFiles != null && !separateProjectFiles) {
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
				}
			} finally {
				zip.close();
			}
		}
		else {
			// save project file to XML
			OutputStream out = getTarget().getOutput();
			try {
				Project.save(project, out);
			} catch (Exception e) {
				reporter.error(new IOMessageImpl("Could not save main project file.", e));
				reporter.setSuccess(false);
				return reporter;
			} finally {
				out.close();
			}
			
			progress.advance(1);
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
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return ProjectIO.PROJECT_TYPE_NAME;
	}

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @return the useSeparateFiles
	 */
	public boolean isUseSeparateFiles() {
		return useSeparateFiles;
	}

	/**
	 * @param useSeparateFiles the useSeparateFiles to set
	 */
	public void setUseSeparateFiles(boolean useSeparateFiles) {
		this.useSeparateFiles = useSeparateFiles;
	}

}
