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

package eu.esdihumboldt.hale.common.core.io.extension;

import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.parameter.IOProviderParameter;

/**
 * Descriptor and factory for an {@link IOProvider}
 * 
 * @author Simon Templer
 */
public interface IOProviderDescriptor extends ExtensionObjectFactory<IOProvider> {

	/**
	 * Get the supported content types
	 * 
	 * @return the set of supported content types
	 */
	public Set<IContentType> getSupportedTypes();

	/**
	 * Get the providers' supported parameters
	 * 
	 * @return the set of supported parameters
	 */
	public Set<IOProviderParameter> getProviderParameter();

	/**
	 * Get the supported configuration content types
	 * 
	 * @return the set of supported configuration content types
	 */
	public Set<IContentType> getConfigurationTypes();

	/**
	 * Get the concrete provider type
	 * 
	 * @return the provider type
	 */
	public Class<? extends IOProvider> getProviderType();

	/**
	 * Get the I/O provider or format description.
	 * 
	 * @return the description or <code>null</code>
	 */
	public String getDescription();

}
