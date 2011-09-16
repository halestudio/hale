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

package eu.esdihumboldt.hale.common.align.transformation.function.impl;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationFunction;

/**
 * Transformation function base class
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public abstract class AbstractTransformationFunction<E extends TransformationEngine> implements
		TransformationFunction<E> {

	private ListMultimap<String, String> parameters;

	/**
	 * @see TransformationFunction#setParameters(ListMultimap)
	 */
	@Override
	public void setParameters(ListMultimap<String, String> parameters) {
		this.parameters = (parameters == null)?(null):(Multimaps.unmodifiableListMultimap(parameters));
	}

	/**
	 * Get the function parameters
	 * @return the parameters, may be <code>null</code> if there are none
	 */
	public ListMultimap<String, String> getParameters() {
		return parameters;
	}

}
