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

import java.io.InputStream;
import java.util.Locale;

import org.eclipse.core.runtime.Platform;
import org.eclipse.help.IHelpContentProducer;

import eu.esdihumboldt.cst.doc.functions.FunctionReferenceConstants;

/**
 * Provides dummy content for function documentation.
 * 
 * @author Simon Templer
 */
public class DummyFunctionReferenceContent implements IHelpContentProducer,
		FunctionReferenceConstants {

	/**
	 * @see IHelpContentProducer#getInputStream(String, String, Locale)
	 */
	@Override
	public InputStream getInputStream(String pluginID, String href,
			Locale locale) {
		if (href.startsWith(FUNCTION_TOPIC_PATH)) {
			// it's a function
			try {
				return Platform.getBundle("eu.esdihumboldt.cst.doc.functions.dummy")
						.getEntry("/html/functions.html").openStream();
			} catch (Throwable e) {
				// ignore
			}
		}

		return null;
	}

}
