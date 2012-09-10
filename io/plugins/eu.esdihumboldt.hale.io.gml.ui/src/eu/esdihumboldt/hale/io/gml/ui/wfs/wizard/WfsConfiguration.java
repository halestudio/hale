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
