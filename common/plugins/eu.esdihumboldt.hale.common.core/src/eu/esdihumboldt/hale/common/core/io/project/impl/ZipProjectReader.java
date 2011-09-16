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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import eu.esdihumboldt.hale.common.core.io.ContentType;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.util.InputStreamDecorator;

/**
 * Reads a project file
 * @author Simon Templer
 */
public class ZipProjectReader extends AbstractImportProvider implements ProjectReader {

	/**
	 * Input stream for a ZIP entry
	 */
	private static class EntryInputStream extends InputStreamDecorator {

		private final ZipInputStream zip;
		
		private final ZipEntry entry;
		
		/**
		 * Create an input stream for a ZIP entry
		 * @param entry the ZIP entry
		 * @param zip the ZIP input stream
		 */
		public EntryInputStream(ZipEntry entry, ZipInputStream zip) {
			super(zip);
			
			this.zip = zip;
			this.entry = entry;
		}

		/**
		 * @see InputStreamDecorator#close()
		 */
		@Override
		public void close() throws IOException {
			// instead of closing the stream, close the entry
			zip.closeEntry();
		}

	}
	
	/**
	 * The additional project files, file names are mapped to project file objects
	 */
	private Map<String, ProjectFile> projectFiles;
	
	/**
	 * The main project file, <code>null</code> if not yet loaded
	 */
	private Project project;

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Load project", ProgressIndicator.UNKNOWN);
		
		projectFiles = new HashMap<String, ProjectFile>();
		project = null;
		
		InputStream in = getSource().getInput();
		ZipInputStream zip = new ZipInputStream(new BufferedInputStream(in));
		try {
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null) {
				String name = entry.getName();
				
				progress.setCurrentTask(MessageFormat.format("Load {0}", name));
				
				if (name.equals(ProjectIO.PROJECT_FILE)) {
					try {
						project = Project.load(new EntryInputStream(entry, zip));
					} catch (Exception e) {
						// fail if main project file cannot be loaded 
						throw new IOProviderConfigurationException(
								"Source is no valid project file", e);
					}
				}
				else if (project != null) {
					Class<? extends ProjectFile> fileClass = project.getFiles().get(name);
					if (fileClass != null) {
						ProjectFile file;
						try {
							file = fileClass.newInstance();
						} catch (Exception e) {
							reporter.error(new IOMessageImpl(
									"Could not create instance of project file class: {0}", 
									e, fileClass.getName()));
							file = null;
						}
						
						if (file != null) {
							try {
								file.load(new EntryInputStream(entry, zip));
							} catch (Exception e) {
								reporter.error(new IOMessageImpl(
										"Error while loading project file {0}, file will be reset.", 
										e, name));
								// reset file
								file.reset();
							}
							
							projectFiles.put(name, file);
						}
					}
					else {
						reporter.error(new IOMessageImpl("Project file {0} has no associated class and cannot be loaded.", 
								null, name));
					}
				}
				else {
					throw new IllegalStateException("Main project file must be first entry.");
				}
			}
		} finally {
			zip.close();
		}
		
		progress.end();
		reporter.setSuccess(true);
		return reporter;
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
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		//TODO change?
		return false;
	}

	/**
	 * @see AbstractIOProvider#getDefaultContentType()
	 */
	@Override
	protected ContentType getDefaultContentType() {
		return ContentType.getContentType(ProjectIO.PROJECT_CT_ID);
	}

}
