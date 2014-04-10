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

package eu.esdihumboldt.hale.common.align.transformation.function.impl;

import java.util.Map;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationFunction;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Base class for implementing {@link PropertyTransformation}s
 * 
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public abstract class AbstractPropertyTransformation<E extends TransformationEngine> extends
		AbstractTransformationFunction<E> implements PropertyTransformation<E> {

	private ListMultimap<String, Object> results;
	private ListMultimap<String, PropertyValue> variables;
	private ListMultimap<String, PropertyEntityDefinition> resultNames;
	private TypeDefinition targetType;
	private Cell cell;
	private Cell typeCell;

	/**
	 * @see PropertyTransformation#setTargetType(TypeDefinition)
	 */
	@Override
	public void setTargetType(TypeDefinition targetType) {
		this.targetType = targetType;
	}

	/**
	 * Get the target type of the instance that is to be populated with the
	 * function result.
	 * 
	 * @return the target instance type
	 */
	protected TypeDefinition getTargetType() {
		return targetType;
	}

	/**
	 * @see PropertyTransformation#setVariables(ListMultimap)
	 */
	@Override
	public void setVariables(ListMultimap<String, PropertyValue> variables) {
		this.variables = Multimaps.unmodifiableListMultimap(variables);
	}

	/**
	 * @see PropertyTransformation#getResults()
	 */
	@Override
	public ListMultimap<String, Object> getResults() {
		return results;
	}

	/**
	 * @see PropertyTransformation#setExpectedResult(ListMultimap)
	 */
	@Override
	public void setExpectedResult(ListMultimap<String, PropertyEntityDefinition> resultNames) {
		this.resultNames = Multimaps.unmodifiableListMultimap(resultNames);
	}

	/**
	 * @see TransformationFunction#execute(String, TransformationEngine, Map,
	 *      TransformationLog, Cell)
	 */
	@Override
	public void execute(String transformationIdentifier, E engine,
			Map<String, String> executionParameters, TransformationLog log, Cell cell)
			throws TransformationException {
		this.cell = cell;
		results = evaluate(transformationIdentifier, engine, variables, resultNames,
				executionParameters, log);
	}

	/**
	 * Get the cell used to configure this function evaluation.
	 * 
	 * @return the cell
	 */
	protected Cell getCell() {
		return cell;
	}

	/**
	 * Execute the evaluation function as configured.
	 * 
	 * @param transformationIdentifier the transformation function identifier
	 * @param engine the transformation engine that may be used for the function
	 *            execution
	 * @param variables the input variables
	 * @param resultNames the expected results (names associated with the
	 *            corresponding entity definitions)
	 * @param executionParameters additional parameters for the execution, may
	 *            be <code>null</code>
	 * @param log the transformation log to report any information about the
	 *            execution of the transformation to
	 * @return the evaluation result
	 * @throws TransformationException if an unrecoverable error occurs during
	 *             transformation
	 */
	protected abstract ListMultimap<String, Object> evaluate(String transformationIdentifier,
			E engine, ListMultimap<String, PropertyValue> variables,
			ListMultimap<String, PropertyEntityDefinition> resultNames,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException;

	/**
	 * Automatic result conversion allowed by default. Override to change this
	 * behavior.
	 * 
	 * @see PropertyTransformation#allowAutomatedResultConversion()
	 */
	@Override
	public boolean allowAutomatedResultConversion() {
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.PropertyTransformation#setTypeCell(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public void setTypeCell(Cell typeCell) {
		this.typeCell = typeCell;
	}

	/**
	 * Get the type cell this property transformation belongs to.
	 * 
	 * @return the cell
	 */
	protected Cell getTypeCell() {
		return typeCell;
	}
}
