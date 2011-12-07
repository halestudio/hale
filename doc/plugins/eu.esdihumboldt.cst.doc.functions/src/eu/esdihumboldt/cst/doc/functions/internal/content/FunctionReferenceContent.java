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

package eu.esdihumboldt.cst.doc.functions.internal.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.help.IHelpContentProducer;

import eu.esdihumboldt.cst.doc.functions.FunctionReferenceConstants;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;

/**
 * Provides content for function documentation.
 * 
 * @author Simon Templer
 */
public class FunctionReferenceContent implements IHelpContentProducer,
		FunctionReferenceConstants {
	
	VelocityEngine ve = new VelocityEngine();

	/**
	 * @see IHelpContentProducer#getInputStream(String, String, Locale)
	 */
	@Override
	public InputStream getInputStream(String pluginID, String href,
			Locale locale) {
		if (href.startsWith(FUNCTION_TOPIC_PATH)) {
			// it's a function
			String func_id = href.substring(FUNCTION_TOPIC_PATH.length());
			try {
				return getFunctionContent(func_id);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	private InputStream getFunctionContent(String func_id) throws Exception {
		Template template = null;

		//creates the temporary file
		File tempFile = null;
		tempFile = File.createTempFile("template", ".vm"); //$NON-NLS-1$ //$NON-NLS-2$
		URL templatePath = this.getClass().getResource("template.html");
		FileOutputStream fos = new FileOutputStream(tempFile);	
		InputStream stream = templatePath.openStream();
		
		// copys the InputStream into FileOutputStream
		IOUtils.copy(stream, fos);
		
		stream.close();
		fos.close();
		
		ve.setProperty("file.resource.loader.path", tempFile.getParent());
		
		// initialize VelocityEngine
		ve.init();

		VelocityContext context = new VelocityContext();
		
		// maps "function" to the real function ID (used by the template)
		AbstractFunction<?> function = FunctionUtil.getFunction(func_id);
		context.put( "function", function);
		
		// creating the full IconURL
		// ------ STARTS HERE ------
		URL url = function.getIconURL();
		
		// "/icons/ICONNAME.png"
		String path = url.getPath();
				
		// "eu.esdihumboldt.cst.functions.TYPE"
		String bundle = function.getDefiningBundle();
		
		StringBuffer sb = new StringBuffer();
		sb.append("PLUGINS_ROOT/");
		sb.append(bundle);
		sb.append(path);
		
		// PLUGINS_ROOT/eu.esdihumboldt.cst.functions.TYPE/icons/ICONNAME.png
		String final_url = sb.toString();
		
		context.put("url", final_url);
		// ------ ENDS HERE ------
		
		template = ve.getTemplate(tempFile.getName(), "UTF-8");

		PipedInputStream pis = new PipedInputStream();
		
		PipedOutputStream pos = new PipedOutputStream(pis);
		
		OutputStreamWriter osw = new OutputStreamWriter(pos, "UTF-8");

		template.merge( context, osw );
		
		osw.close();
		
		return pis;

	}

}
