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

package eu.esdihumboldt.hale.common.instance.extension.metadata;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;

/**
 * Factory Interface for {MetadataAction}s
 * @author Sebastian Reinhardt
 */
public interface MetadataActionFactory extends ExtensionObjectFactory<MetadataAction> {

	/**
	 * returns the meta data key used for the factory
	 * @return the meta data key
	 */
	public String getKey();
}
