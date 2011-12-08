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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.help.IHelpContentProducer;

import eu.esdihumboldt.cst.doc.functions.FunctionReferenceConstants;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;

import com.google.common.io.Files;

/**
 * Provides content for function documentation.
 * 
 * @author Simon Templer
 */
public class FunctionReferenceContent implements IHelpContentProducer,
		FunctionReferenceConstants {

	private VelocityEngine ve;
	private File tempDir;

	/**
	 * @see IHelpContentProducer#getInputStream(String, String, Locale)
	 */
	@Override
	public InputStream getInputStream(String pluginID, String href,
			Locale locale) {
		if (href.startsWith(FUNCTION_TOPIC_PATH)) {
			// it's a function
			String func_id = href.substring(FUNCTION_TOPIC_PATH.length());
			// strip the .*htm? ending
			if (func_id.endsWith("html") || func_id.endsWith("htm")) {
				func_id = func_id.substring(0, func_id.lastIndexOf('.'));
			}
			try {
				return getFunctionContent(func_id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private InputStream getFunctionContent(String func_id) throws Exception {
		Template template = null;

		synchronized (this) {
			if (ve == null) {
				ve = new VelocityEngine();
				// create a temporary directory
				tempDir = Files.createTempDir();
				ve.setProperty("file.resource.loader.path", tempDir.getAbsolutePath());
				// initialize VelocityEngine
				ve.init();
			}
		}

		// creates the temporary file into the temporary directory
		File tempFile = null;
		tempFile = File.createTempFile("template", ".vm", tempDir); //$NON-NLS-1$ //$NON-NLS-2$
		URL templatePath = this.getClass().getResource("template.html");
		FileOutputStream fos = new FileOutputStream(tempFile);
		InputStream stream = templatePath.openStream();

		// copys the InputStream into FileOutputStream
		IOUtils.copy(stream, fos);

		stream.close();
		fos.close();

		VelocityContext context = new VelocityContext();

		// maps "function" to the real function ID (used by the template)
		AbstractFunction<?> function = FunctionUtil.getFunction(func_id);
		context.put("function", function);

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

		// creating path for the file to be included
		URL help_url = function.getHelpURL();
		if (help_url != null) {
			String help_path = help_url.getPath();

			//String help_id = function.getHelpFileID();

			StringBuffer sb_include = new StringBuffer();
			// sb_include.append("PLUGINS_ROOT/");
			sb_include.append(bundle);
			sb_include.append(help_path);
			sb_include.append("/help");
			// sb_include.append(help_id);

			String final_help_url = sb_include.toString();

			context.put("include", final_help_url);
		}
		
		// TODO: getting parameters
		Set<FunctionParameter> params = function.getDefinedParameters();
		Iterator<FunctionParameter> it = params.iterator();
		ArrayList<FunctionParameter> funcparams = new ArrayList<FunctionParameter>();
		while(it.hasNext()) {
			funcparams.add(it.next());
		}
		String bla = "funcparams is leer";
		if(funcparams != null) {
			// FunctionOutOfBoundsException if there are no parameters for the function
			bla = funcparams.get(0).getName();
		}
//		System.out.println(bla);
		template = ve.getTemplate(tempFile.getName(), "UTF-8");

		PipedInputStream pis = new PipedInputStream();

		PipedOutputStream pos = new PipedOutputStream(pis);

		OutputStreamWriter osw = new OutputStreamWriter(pos, "UTF-8");

		template.merge(context, osw);

		osw.close();

		return pis;

	}

}
