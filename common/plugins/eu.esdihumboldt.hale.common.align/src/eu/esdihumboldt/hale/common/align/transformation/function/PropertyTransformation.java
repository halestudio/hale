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

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.service.PropertyTransformer;

/**
 * Function that is evaluated based on variables populated by property values.
 * 
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public interface PropertyTransformation<E extends TransformationEngine> extends
		TransformationFunction<E> {

	/**
	 * Set the property values serving as variables for the function.
	 * 
	 * @param variables the property values, variable names are mapped to
	 *            property values
	 */
	public void setVariables(ListMultimap<String, PropertyValue> variables);

	/**
	 * Set the expected result names.
	 * 
	 * @param resultNames the names of the expected results associated with the
	 *            corresponding entity definition
	 */
	public void setExpectedResult(ListMultimap<String, PropertyEntityDefinition> resultNames);

	/**
	 * Get the
	 * {@link #execute(String, TransformationEngine, Map, TransformationLog)}ion
	 * results.
	 * 
	 * @return the execution results, result names are mapped to result values
	 * @see #setExpectedResult(ListMultimap)
	 */
	public ListMultimap<String, Object> getResults();

	/**
	 * Specifies if the automatic conversion of the execution results according
	 * to the corresponding property definitions is allowed and therefore should
	 * be performed by the {@link PropertyTransformer}.
	 * 
	 * @return if automated conversion of the result values is allowed
	 */
	public boolean allowAutomatedResultConversion();

}
