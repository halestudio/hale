/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.io.config;

import java.util.Set;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;

/**
 * Factory for implementations of {@link AbstractConfigurationDialog}
 * 
 * @author Florian Esser
 */
public interface ConfigurationDialogFactory
		extends ExtensionObjectFactory<AbstractConfigurationDialog> {

	/**
	 * Get the identifiers of the supported providers
	 * 
	 * @return the set of supported provider identifiers
	 */
	public Set<String> getSupportedProviderIDs();
}
