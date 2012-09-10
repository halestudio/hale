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

package eu.esdihumboldt.hale.io.gml.ui.wfs.wizard;

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
 */
public class WfsGetFeatureConfiguration extends WfsConfiguration {

	private List<String> filters;

	/**
	 * @see WfsConfiguration#WfsConfiguration(String)
	 */
	public WfsGetFeatureConfiguration(String fixedNamespace) {
		super(fixedNamespace);
	}

	/**
	 * @return the filters
	 */
	public List<String> getFilters() {
		return filters;
	}

	/**
	 * @param filters the filters to set
	 */
	public void setFilters(List<String> filters) {
		this.filters = filters;
	}

	/**
	 * Get the request URL
	 * 
	 * @return the request URL
	 * 
	 * @throws UnsupportedEncodingException if UTF-8 encoding is not supported
	 * @throws MalformedURLException if the getFeature request URL is malformed
	 */
	public URL getRequestURL() throws UnsupportedEncodingException, MalformedURLException {
		String capabilities = getCapabilitiesURL();

		String getFeature = null;
		int x = capabilities.toLowerCase().indexOf("request=getcapabilities"); //$NON-NLS-1$
		if (x >= 0) {
			String repl = capabilities.substring(x, x + "request=getcapabilities".length()); //$NON-NLS-1$
			getFeature = capabilities.replace(repl, "REQUEST=GetFeature"); //$NON-NLS-1$
		}

		if (getFeature != null) {
			StringBuffer typeNames = new StringBuffer();
			StringBuffer filterString = new StringBuffer();

			boolean filterPresent = false;

			boolean first = true;
			List<FeatureType> types = getFeatureTypes();
			if (types != null && !types.isEmpty()) {
				for (int i = 0; i < types.size(); i++) {
					FeatureType type = types.get(i);
					if (first) {
						first = false;
					}
					else {
						typeNames.append(',');
					}

					String typeName = type.getName().getLocalPart();
					typeNames.append(typeName);

					String filter = null;
					if (filters != null && filters.size() > i) {
						filter = filters.get(i);
					}
					if (types.size() > 1) {
						filterString.append('(');
					}
					filterString.append((filter == null) ? ("") : (filter)); //$NON-NLS-1$
					if (types.size() > 1) {
						filterString.append(')');
					}

					if (filter != null && !filter.isEmpty()) {
						filterPresent = true;
					}
				}
			}
			else
				throw new IllegalArgumentException("No types specified"); //$NON-NLS-1$

			// types
			getFeature = getFeature
					.concat("&TYPENAME=" + URLEncoder.encode(typeNames.toString(), "UTF-8")); //$NON-NLS-1$ //$NON-NLS-2$

			// filters
			if (filterPresent) {
				getFeature = getFeature
						.concat("&FILTER=" + URLEncoder.encode(filterString.toString(), "UTF-8")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		// get the URL
		return new URL(getFeature);
	}

}
