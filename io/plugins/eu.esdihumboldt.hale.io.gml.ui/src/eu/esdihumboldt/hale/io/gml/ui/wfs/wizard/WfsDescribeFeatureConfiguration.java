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

package eu.esdihumboldt.hale.io.gml.ui.wfs.wizard;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import org.geotools.data.DataStore;
import org.geotools.data.wfs.WFSDataStore;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.io.gml.ui.wfs.wizard.capabilities.GetCapabilititiesRetriever;

/**
 * Configuration for WFS DescribeFeature
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class WfsDescribeFeatureConfiguration extends WfsConfiguration {

	/**
	 * Default constructor
	 */
	public WfsDescribeFeatureConfiguration() {
		super(null);
	}

	/**
	 * Get the request URL
	 * 
	 * @return the request URL
	 * @throws IOException if getting the WFS capabilities failed
	 */
	public URL getRequestURL() throws IOException {
		String capabilities = getCapabilitiesURL();

		// build DescribeFeatureType URL
		DataStore data = GetCapabilititiesRetriever.getDataStore(capabilities);

		// collect type names
		StringBuffer typeNames = new StringBuffer();
		String firstType = null;
		boolean first = true;
		for (FeatureType type : getFeatureTypes()) {
			String typeName = type.getName().getLocalPart();

			if (first) {
				first = false;
				firstType = typeName;
			}
			else {
				typeNames.append(',');
			}
			typeNames.append(typeName);
		}

		// get the URL
		// XXX replaced by code below - url_result = ((WFSDataStore)
		// data).getDescribeFeatureTypeURL(typeNames.toString());
		// XXX we have to trick because the geotools implementation of the WFS
		// 1.1.0 protocol is limited to one feature type
		// TODO better solution
		if (firstType != null) {
			String temp = ((WFSDataStore) data).getDescribeFeatureTypeURL(firstType).toString();
			String repl = URLEncoder.encode(firstType, "UTF-8");
			if (temp.indexOf(repl) < 0) {
				repl = firstType;
			}
			temp = temp.replaceAll(repl, URLEncoder.encode(typeNames.toString(), "UTF-8"));
			return new URL(temp);
		}
		else {
			throw new IllegalArgumentException("No types specified"); //$NON-NLS-1$
		}
	}

}
