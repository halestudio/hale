/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.htmlexporter;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Vector;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportException;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportProvider;

/**
 * Export a Mapping to HTML for documentation purposes.
 * 
 * @author Stefan Gessner
 * @version $Id$
 */
public class HtmlMappingExportFactory 
	implements MappingExportProvider {


	/**
     * @param alignment
	 * @param path
	 * @throws MappingExportException
	 * @see eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportProvider#export(eu.esdihumboldt.goml.align.Alignment, java.lang.String)
	 */
	public void export(Alignment alignment, String path) throws MappingExportException {
		
		
		StringWriter sw = new StringWriter();
		String p1 = "Bill";
		String p2 = "Bob";
		Vector vec = new Vector();
		vec.addElement( p1 );
		vec.addElement( p2 );
		URL templatePath = this.getClass().getResource("/resources/template.vm"); 
//		URL templatePath = this.getClass().getResource("/WEB-INF/template.vm"); 
//		Velocity.addProperty("resource.loader", "MYO");
//		Velocity.addProperty(Velocity.RESOURCE_LOADER, new org.apache.velocity.runtime.resource.loader.URLResourceLoader());
//		Velocity.addProperty("MYO.resource.loader.root", "");
//		Velocity.setProperty("resource.loader", "MYO");
//		Velocity.setProperty("MYO.resource.loader.class", "org.apache.velocity.runtime.resource.loader.URLResourceLoader");
//		Velocity.setProperty("MYO.resource.loader.root", "");
			try {

				Velocity.init();
//				Velocity.addProperty("resource.loader", "MYO");
//				Velocity.addProperty("MYO.resource.loader.class", new org.apache.velocity.runtime.resource.loader.URLResourceLoader());
//				Velocity.addProperty("MYO.resource.loader.root", "");
//				Velocity.setProperty("resource.loader", "MYO");
//				Velocity.setProperty("MYO.resource.loader.class", new org.apache.velocity.runtime.resource.loader.URLResourceLoader());
//				Velocity.setProperty("MYO.resource.loader.root", "");
				
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			VelocityContext context = new VelocityContext();
			context.put("list", vec );
			Template template = null;
			
			try {
				if(templatePath!=null){
					template = Velocity.getTemplate(templatePath.toString());
				}
			} catch (ResourceNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				template.merge(context,sw);
			} catch (ResourceNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MethodInvocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
}
