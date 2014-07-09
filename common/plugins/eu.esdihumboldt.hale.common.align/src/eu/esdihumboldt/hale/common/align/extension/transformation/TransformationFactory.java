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

package eu.esdihumboldt.hale.common.align.extension.transformation;

import java.util.Map;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
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
