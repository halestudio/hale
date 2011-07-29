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

package eu.esdihumboldt.hale.align.extension.function;

import java.util.Set;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;

/**
 * Basic interface for function definitions
 * @author Simon Templer
 */
public interface Function extends Identifiable {
	
	/**
	 * Get the human readable name of the function
	 * @return the function name
	 */
	public String getDisplayName();
	
	/**
	 * Get the function description
	 * @return the description, may be <code>null</code>
	 */
	public String getDescription();
	
	/**
	 * Get the ID of the function's category
	 * @return the category ID, may be <code>null</code>
	 */
	public String getCategoryId();
	
	/**
	 * Get the defined parameters for the function
	 * @return the defined parameters
	 */
	public Set<FunctionParameter> getDefinedParameters();

}
