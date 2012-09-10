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

package eu.esdihumboldt.hale.common.align.extension.transformation;

import java.util.Map;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationFunction;

/**
 * Factory for {@link TransformationFunction}s
 * 
 * @param <T> the concrete transformation function type
 * 
 * @author Simon Templer
 */
public interface TransformationFactory<T extends TransformationFunction<?>> extends
		ExtensionObjectFactory<T> {

	/**
	 * Get the identifier of the engine the transformation must be executed
	 * with.
	 * 
	 * @return the engine ID or <code>null</code>
	 */
	public String getEngineId();

	/**
	 * Get the identifier of the function the transformation implements.
	 * 
	 * @return the ID of the associated function
	 */
	public String getFunctionId();

	/**
	 * Get the execution parameters for the transformation
	 * 
	 * @return the defined execution parameters
	 */
	public Map<String, String> getExecutionParameters();

}
