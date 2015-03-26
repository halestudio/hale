/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.wfs.ui.legacy.wizard;

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

			return new URL(getFeature);
		}
		else {
			throw new IllegalStateException("Request URL could not be determined");
		}
	}

}
