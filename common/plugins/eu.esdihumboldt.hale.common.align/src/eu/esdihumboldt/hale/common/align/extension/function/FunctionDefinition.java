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

package eu.esdihumboldt.hale.common.align.extension.function;

import java.net.URL;
import java.util.Collection;
import java.util.Set;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;

/**
 * Basic interface for function definitions
 * 
 * @param <P> entity parameter definition type
 * @author Simon Templer
 */
public interface FunctionDefinition<P extends ParameterDefinition> extends Identifiable {

	/**
	 * Get the human readable name of the function
	 * 
	 * @return the function name
	 */
	public String getDisplayName();

	/**
	 * Get the function description
	 * 
	 * @return the description, may be <code>null</code>
	 */
	public String getDescription();

	/**
	 * Get the ID of the function's category
	 * 
	 * @return the category ID, may be <code>null</code>
	 */
	public String getCategoryId();

	/**
	 * States if the function represents an augmentation of a target instance
	 * instead of a transformation.
	 * 
	 * @return if the function is an augmentation
	 */
	public boolean isAugmentation();

	/**
	 * Get the defined parameters for the function
	 * 
	 * @return the defined parameters
	 */
	public Collection<FunctionParameterDefinition> getDefinedParameters();

	/**
	 * Get the function parameter with the given name.
	 * 
	 * @param paramName the parameter name
	 * @return the parameter or <code>null</code> if it doesn't exist
	 */
	public FunctionParameterDefinition getParameter(String paramName);

	/**
	 * Get the icon URL
	 * 
	 * @return the icon URL, may be <code>null</code>
	 */
	public URL getIconURL();

	/**
	 * Get the symbolic name of the bundle defining the function.
	 * 
	 * @return the bundle symbolic name
	 */
	public String getDefiningBundle();

	/**
	 * Get the help file URL
	 * 
	 * @return the help file URL, may be <code>null</code>
	 */
	public URL getHelpURL();

	/**
	 * Get the associated cell explanation.
	 * 
	 * @return the cell explanation or <code>null</code> if none is available
	 *         for this function
	 */
	public CellExplanation getExplanation();

	/**
	 * Get the source entities
	 * 
	 * @return the source entities
	 */
	public Set<? extends P> getSource();

	/**
	 * Get the target entities
	 * 
	 * @return the target entities
	 */
	public Set<? extends P> getTarget();

//	/**
//	 * Get the help file ID of the text to be included
//	 * @return the help file ID, my be <code>null</code>
//	 */
//	public String getHelpFileID();

}
