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

package eu.esdihumboldt.hale.ui.io.config;

import java.util.Set;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;

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
