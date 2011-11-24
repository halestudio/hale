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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.eclipse.help.IHelpContentProducer;

import eu.esdihumboldt.cst.doc.functions.FunctionReferenceConstants;

/**
 * TODO Type description
 * @author Simon Templer
 */
public class FunctionReferenceContent implements IHelpContentProducer, FunctionReferenceConstants {

	/**
	 * @see IHelpContentProducer#getInputStream(String, String, Locale)
	 */
	@Override
	public InputStream getInputStream(String pluginID, String href,
			Locale locale) {
		if (href.startsWith(FUNCTION_TOPIC_PATH)) {
			// it's a function
			//TODO return filled template for function doc
			try {
				return new ByteArrayInputStream(href.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		return null;
	}

}
