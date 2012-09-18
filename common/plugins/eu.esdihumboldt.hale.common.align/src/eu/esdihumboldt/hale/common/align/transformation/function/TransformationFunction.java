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

package eu.esdihumboldt.hale.common.align.transformation.function;

import java.util.Map;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;

/**
 * Common interface for all transformation functions
 * 
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public interface TransformationFunction<E extends TransformationEngine> {

	/**
	 * Sets the parameters for the transformation.
	 * 
	 * @param parameters the transformation parameters
	 */
	public void setParameters(ListMultimap<String, String> parameters);

	/**
	 * Set the execution context.
	 * 
	 * @param executionContext the execution context of the transformation
	 *            process
	 */
	public void setExecutionContext(ExecutionContext executionContext);

	/**
	 * Execute the function as configured.
	 * 
	 * @param transformationIdentifier the transformation function identifier
	 * @param engine the transformation engine that may be used for the function
	 *            execution
	 * @param executionParameters additional parameters for the execution, may
	 *            be <code>null</code>
	 * @param log the transformation log to report any information about the
	 *            execution of the transformation to
	 * @throws TransformationException if an unrecoverable error occurs during
	 *             transformation
	 */
	public void execute(String transformationIdentifier, E engine,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException;

	// TODO reset method? or something like it

}
