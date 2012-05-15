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

package eu.esdihumboldt.hale.doc.user.examples.internal.content;

import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.Callable;

import org.apache.velocity.VelocityContext;
import org.eclipse.help.IHelpContentProducer;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

import eu.esdihumboldt.hale.doc.user.examples.internal.ExamplesConstants;
import eu.esdihumboldt.hale.doc.user.examples.internal.extension.ExampleProjectExtension;
import eu.esdihumboldt.hale.doc.util.content.AbstractVelocityContent;

/**
 * Examples content producer.
 * @author Simon Templer
 */
public class ExamplesContent extends AbstractVelocityContent implements ExamplesConstants {
	
	private static final ALogger log = ALoggerFactory.getLogger(ExamplesContent.class);
	
	private static final String TEMPLATE_OVERVIEW = "overview";
	
	private static final String TEMPLATE_PROJECT = "project";
	
	/**
	 * @see IHelpContentProducer#getInputStream(String, String, Locale)
	 */
	@Override
	public InputStream getInputStream(String pluginID, String href,
			Locale locale) {
		if (href.startsWith(PATH_PREFIX_PROJECT)) {
			// references a project
			
			// determine the project id
			String projectId = href.substring(PATH_PREFIX_PROJECT.length());
			// strip everything after a ?
			int ind = projectId.indexOf('?');
			if (ind >= 0) {
				projectId = projectId.substring(0, ind);
			}
			// strip the .*htm? ending
			if (projectId.endsWith("html") || projectId.endsWith("htm")) {
				projectId = projectId.substring(0, projectId.lastIndexOf('.'));
			}
			
			return getProjectContent(projectId);
		}
		else if (href.startsWith(PATH_OVERVIEW)) {
			return getOverviewContent();
		}
		
		return null;
	}

	/**
	 * Create the overview content.
	 * @return the overview page content
	 */
	private InputStream getOverviewContent() {
		try {
			return getContentFromTemplate("overview", TEMPLATE_OVERVIEW, new Callable<VelocityContext>() {

				@Override
				public VelocityContext call() throws Exception {
					VelocityContext context = new VelocityContext();
					
					context.put("projects", ExampleProjectExtension.getInstance().getElements());
					
					return context;
				}
			});
		} catch (Exception e) {
			log.error("Error creating example project overview", e);
			return null;
		}
	}

	/**
	 * Get the project page content. 
	 * @param projectId the project ID
	 * @return the project page content
	 */
	private InputStream getProjectContent(final String projectId) {
		try {
			return getContentFromTemplate(projectId, TEMPLATE_PROJECT, new Callable<VelocityContext>() {

				@Override
				public VelocityContext call() throws Exception {
					VelocityContext context = new VelocityContext();
					
					context.put("project", ExampleProjectExtension.getInstance().get(projectId));
					
					return context;
				}
			});
		} catch (Exception e) {
			log.error("Error creating project page", e);
			return null;
		}
	}

	/**
	 * @see AbstractVelocityContent#getTemplate(String)
	 */
	@Override
	protected InputStream getTemplate(String templateId) throws Exception {
		if (TEMPLATE_PROJECT.equals(templateId)) {
			return ExamplesContent.class.getResourceAsStream("project.html");
		}
		return ExamplesContent.class.getResourceAsStream(PATH_OVERVIEW);
	}

}
