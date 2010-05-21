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

package eu.esdihumboldt.hale.rcp.wizards.io.wfs;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.opengis.feature.type.FeatureType;

/**
 * Configuration for WFS GetFeature
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class WfsGetFeatureConfiguration extends WfsConfiguration {
	
	/**
	 * @see WfsConfiguration#WfsConfiguration(String)
	 */
	public WfsGetFeatureConfiguration(String fixedNamespace) {
		super(fixedNamespace);
	}

	/**
	 * Get the request URL
	 *  
	 * @return the request URL 
	 * 
	 * @throws UnsupportedEncodingException 
	 * @throws MalformedURLException 
	 */
	public URL getRequestURL() throws UnsupportedEncodingException, MalformedURLException {
		String capabilities = getCapabilitiesURL();
		
		String getFeature = null;
		int x = capabilities.toLowerCase().indexOf("request=getcapabilities");
		if (x >= 0) {
			String repl = capabilities.substring(x, x + "request=getcapabilities".length());
			getFeature = capabilities.replace(repl, "REQUEST=GetFeature");
		}
		
		if (getFeature != null) {
			StringBuffer typeNames = new StringBuffer();
			boolean first = true;
			List<FeatureType> types = getFeatureTypes();
			if (types != null && !types.isEmpty()) {
				for (FeatureType type : types) {
					String typeName = type.getName().getLocalPart();
					if (first) {
						first = false;
					}
					else {
						typeNames.append(',');
					}
					typeNames.append(typeName);
				}
			}
			else throw new IllegalArgumentException("No types specified");
			
			getFeature = getFeature.concat("&TYPENAME=" + URLEncoder.encode(typeNames.toString(), "UTF-8"));
			
			//TODO filtering
			/*if (!filterText.getStringValue().isEmpty()) {
				getFeature = getFeature.concat("&FILTER=" + filterText.getStringValue());
			}*/
		}
		
		// get the URL
		return new URL(getFeature);
	}

}
