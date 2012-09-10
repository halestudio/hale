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

package eu.esdihumboldt.hale.common.core.io.extension;

import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;

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
	 * Get the concrete provider type
	 * 
	 * @return the provider type
	 */
	public Class<? extends IOProvider> getProviderType();

}
