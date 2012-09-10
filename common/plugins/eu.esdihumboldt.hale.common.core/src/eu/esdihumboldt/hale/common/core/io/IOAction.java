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

package eu.esdihumboldt.hale.common.core.io;

import java.util.Set;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;

/**
 * Represents an I/O action
 * 
 * @author Simon Templer
 */
public interface IOAction extends Identifiable {

	/**
	 * Get the I/O provider type supported by the action.
	 * 
	 * @return the I/O provider type
	 */
	public Class<? extends IOProvider> getProviderType();

	/**
	 * Get the dependencies of the action.
	 * 
	 * @return the list of identifiers of other actions the action depends on
	 *         for sequential execution, e.g. when loading a project
	 */
	public Set<String> getDependencies();

	/**
	 * Get the action name
	 * 
	 * @return the name, may be <code>null</code>
	 */
	public String getName();

}
