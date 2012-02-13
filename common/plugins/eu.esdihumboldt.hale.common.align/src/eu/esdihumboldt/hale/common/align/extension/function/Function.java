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

package eu.esdihumboldt.hale.common.align.extension.function;

import java.net.URL;
import java.util.Set;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;

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
	
	/**
	 * Get the icon URL
	 * @return the icon URL, may be <code>null</code>
	 */
	public URL getIconURL();
	
	/**
	 * Get the symbolic name of the bundle defining the function.
	 * @return the bundle symbolic name
	 */
	public String getDefiningBundle();
	
	/**
	 * Get the help file URL
	 * @return the help file URL, may be <code>null</code>
	 */
	public URL getHelpURL();
	
	/**
	 * Get the associated cell explanation.
	 * @return the cell explanation or <code>null</code> if none is available
	 *   for this function
	 */
	public CellExplanation getExplanation();
	
	/**
	 * Get the source entities
	 * @return the source entities
	 */
	public Set<? extends AbstractParameter> getSource();
	
	/**
	 * Get the target entities
	 * @return the target entities
	 */
	public Set<? extends AbstractParameter> getTarget();
	
//	/**
//	 * Get the help file ID of the text to be included
//	 * @return the help file ID, my be <code>null</code>
//	 */
//	public String getHelpFileID();

}
