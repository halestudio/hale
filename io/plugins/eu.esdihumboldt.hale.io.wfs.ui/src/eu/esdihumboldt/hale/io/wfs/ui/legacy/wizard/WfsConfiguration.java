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

import java.util.List;

import org.opengis.feature.type.FeatureType;

/**
 * 
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class WfsConfiguration {

	private final String fixedNamespace;

	private String capabilitiesURL;

	private List<FeatureType> featureTypes;

	/**
	 * Constructor
	 * 
	 * @param fixedNamespace the fixed namespace or <code>null</code>
	 */
	public WfsConfiguration(String fixedNamespace) {
		super();
		this.fixedNamespace = fixedNamespace;
	}

	/**
	 * @return the fixedNamespace
	 */
	public String getFixedNamespace() {
		return fixedNamespace;
	}

	/**
	 * @return the capabilitiesURL
	 */
	public String getCapabilitiesURL() {
		return capabilitiesURL;
	}

	/**
	 * @param capabilitiesURL the capabilitiesURL to set
	 */
	public void setCapabilitiesURL(String capabilitiesURL) {
		this.capabilitiesURL = capabilitiesURL;
	}

	/**
	 * @return the featureTypes
	 */
	public List<FeatureType> getFeatureTypes() {
		return featureTypes;
	}

	/**
	 * @param featureTypes the featureTypes to set
	 */
	public void setFeatureTypes(List<FeatureType> featureTypes) {
		this.featureTypes = featureTypes;
	}

	/**
	 * Validate the settings
	 * 
	 * @return <code>true</code> if the settings are valid, <code>false</code>
	 *         otherwise
	 */
	public boolean validateSettings() {
		// TODO
		return true;
	}

}
