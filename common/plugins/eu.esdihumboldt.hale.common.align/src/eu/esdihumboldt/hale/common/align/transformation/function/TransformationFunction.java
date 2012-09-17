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
