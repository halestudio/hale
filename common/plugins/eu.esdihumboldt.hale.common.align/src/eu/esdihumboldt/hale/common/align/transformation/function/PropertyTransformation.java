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

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.service.PropertyTransformer;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

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
	 * Set the target type of the created instance that is to be populated with
	 * the function result.
	 * 
	 * @param targetType the type of the target instance
	 */
	public void setTargetType(TypeDefinition targetType);

	/**
	 * Set the expected result names.
	 * 
	 * @param resultNames the names of the expected results associated with the
	 *            corresponding entity definition
	 */
	public void setExpectedResult(ListMultimap<String, PropertyEntityDefinition> resultNames);

	/**
	 * Get the
	 * {@link #execute(String, TransformationEngine, Map, TransformationLog, Cell)}
	 * ion results.
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

	/**
	 * Set the type cell this property transformation belongs to.
	 * 
	 * @param typeCell the type cell
	 */
	public void setTypeCell(Cell typeCell);

}
