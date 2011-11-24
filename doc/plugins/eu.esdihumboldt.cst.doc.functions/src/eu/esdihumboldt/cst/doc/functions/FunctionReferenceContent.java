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

package eu.esdihumboldt.cst.doc.functions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.eclipse.help.IHelpContentProducer;

/**
 * TODO Type description
 * @author Simon Templer
 */
public class FunctionReferenceContent implements IHelpContentProducer {

	/**
	 * @see org.eclipse.help.IHelpContentProducer#getInputStream(java.lang.String, java.lang.String, java.util.Locale)
	 */
	@Override
	public InputStream getInputStream(String pluginID, String href,
			Locale locale) {
		try {
			return new ByteArrayInputStream(href.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		// TODO Auto-generated method stub
//		return null;
	}

}
