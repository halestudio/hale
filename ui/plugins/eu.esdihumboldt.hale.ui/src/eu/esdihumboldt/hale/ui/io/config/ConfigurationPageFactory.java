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

package eu.esdihumboldt.hale.ui.io.config;

import java.util.Set;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
public interface ConfigurationPageFactory extends
		ExtensionObjectFactory<AbstractConfigurationPage<?, ?>> {

	/**
	 * Get the identifiers of the supported providers
	 * 
	 * @return the set of supported provider identifiers
	 */
	public Set<String> getSupportedProviderIDs();

}
