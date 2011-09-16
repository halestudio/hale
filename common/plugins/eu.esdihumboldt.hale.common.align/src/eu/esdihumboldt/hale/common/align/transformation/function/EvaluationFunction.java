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
import com.google.common.collect.Multiset;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;

/**
 * Function that is evaluated based on variables populated by property values.
 * @param <E> the transformation engine type
 *  
 * @author Simon Templer
 */
public interface EvaluationFunction<E extends TransformationEngine> extends TransformationFunction<E> {
	
	/**
	 * Represents a property value for use in an {@link EvaluationFunction}
	 */
	public static class PropertyValue {
		
		private final Object value;
		
		private final PropertyEntityDefinition property;
		
		/**
		 * Create a property value associated with its definition
		 * @param value the property value
		 * @param property the property entity definition
		 */
		public PropertyValue(Object value, PropertyEntityDefinition property) {
			super();
			this.value = value;
			this.property = property;
		}

		/**
		 * Get the property value.
		 * @return the property value
		 */
		public Object getValue() {
			return value;
		}

		/**
		 * Get the property.
		 * @return the property  entity definition
		 */
		public PropertyEntityDefinition getProperty() {
			return property;
		}

	}
	
	/**
	 * Set the property values serving as variables for the function.
	 * @param variables the property values, variable names are mapped to
	 *   property values
	 */
	public void setVariables(ListMultimap<String, PropertyValue> variables);
	
	/**
	 * Set the expected result names
	 * @param resultNames the names of the expected results
	 */
	public void setExpectedResult(Multiset<String> resultNames);
	
	/**
	 * Get the {@link #execute(String, TransformationEngine, Map, TransformationLog)}ion results.
	 * @return the execution results, result names are mapped to result values
	 * @see #setExpectedResult(Multiset)
	 */
	public ListMultimap<String, Object> getResults();

}
