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

package eu.esdihumboldt.hale.doc.util.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.help.IHelpContentProducer;

import com.google.common.io.Files;

/**
 * Help content based on velocity templates. Use
 * {@link #getContentFromTemplate(VelocityContext, String)} in your
 * {@link #getInputStream(String, String, Locale)} implementation.
 * 
 * @author Simon Templer
 */
public abstract class AbstractVelocityContent implements IHelpContentProducer {
	
	/**
	 * The velocity engine used for content generation.
	 */
	private VelocityEngine ve;
	
	/**
	 * The temporary directory where the template is copied to, and the
	 * already generated is stored.
	 */
	private File tempDir;

	/**
	 * Generate content from the template and the given context factory. If 
	 * called more than once with the same id, the previously generated content
	 * for that id is returned.
	 * @param contentId the content id or <code>null</code>
	 * @param contextFactory the context factory, is called once or not at all
	 * @return the content input stream to return in {@link #getInputStream(String, String, Locale)}
	 * @throws Exception if an error occurs creating the content
	 */
	protected InputStream getContentFromTemplate(String contentId, String templateId, 
			Callable<VelocityContext> contextFactory) throws Exception {
		init(templateId);
		
		// creates the template file into the temporary directory
		// if it doesn't already exist
		File contentFile = (contentId == null)?(null):(new File(tempDir, templateId + "_" + contentId + ".html"));
		if (contentFile == null || !contentFile.exists()) {
			// get the template context
			VelocityContext context = contextFactory.call();

			// get the template
			Template template = ve.getTemplate(templateId + ".vm", "UTF-8");

			// write to the file
			FileWriter fw = new FileWriter(contentFile);
			template.merge(context, fw);

			fw.close();

			contentFile.deleteOnExit();
		}

		return new FileInputStream(contentFile);
	}

	/**
	 * Initialize temporary directory and template engine.
	 * 
	 * @throws Exception
	 *             if an error occurs during the initialization
	 */
	private void init(String templateId) throws Exception {
		synchronized (this) {
			// engine initialization
			if (ve == null) {
				ve = new VelocityEngine();
				// create a temporary directory
				tempDir = Files.createTempDir();
				tempDir.deleteOnExit();

				ve.setProperty("file.resource.loader.path",
						tempDir.getAbsolutePath());
				
				// initialize VelocityEngine
				ve.init();
			}
			
			File templateFile = new File(tempDir, templateId + ".vm");
			if (!templateFile.exists()) {
				FileOutputStream fos = new FileOutputStream(templateFile);
				InputStream stream = getTemplate(templateId);
	
				// copy the InputStream into FileOutputStream
				IOUtils.copy(stream, fos);
	
				stream.close();
				fos.close();
				
				templateFile.deleteOnExit();
			}
		}
	}
	
	/**
	 * Get the template content.
	 * @return the template as input stream
	 * @throws Exception if an error occurs retrieving the template
	 */
	protected abstract InputStream getTemplate(String templateId) throws Exception;

}
