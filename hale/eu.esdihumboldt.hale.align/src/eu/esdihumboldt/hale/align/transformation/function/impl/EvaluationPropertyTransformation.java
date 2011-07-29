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

package eu.esdihumboldt.hale.align.transformation.function.impl;

import java.util.Map;

import eu.esdihumboldt.hale.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.align.transformation.function.EvaluationFunction;
import eu.esdihumboldt.hale.align.transformation.function.PropertyTransformation;
import eu.esdihumboldt.hale.align.transformation.function.TransformationFunction;
import eu.esdihumboldt.hale.align.transformation.report.TransformationLog;

/**
 * {@link PropertyTransformation} wrapping an {@link EvaluationFunction}
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public class EvaluationPropertyTransformation<E extends TransformationEngine>
		extends AbstractPropertyTransformation<E> {
	
	/**
	 * Create a property transformation based on the given evaluator
	 * @param evaluator the evaluator
	 * @return the property transformation
	 */
	public static <T extends TransformationEngine> PropertyTransformation<T> createTransformation(
			EvaluationFunction<T> evaluator) {
		return new EvaluationPropertyTransformation<T>(evaluator);
	}

	private final EvaluationFunction<E> evaluator;

	/**
	 * Create a property transformation based on the given evaluator
	 * @param evaluator the evaluator
	 */
	public EvaluationPropertyTransformation(EvaluationFunction<E> evaluator) {
		super();
		this.evaluator = evaluator;
	}

	/**
	 * @see TransformationFunction#execute(String, TransformationEngine, Map, TransformationLog)
	 */
	@Override
	public void execute(String transformationIdentifier, E engine,
			Map<String, String> executionParameters, TransformationLog log) {
		// TODO Auto-generated method stub
		//FIXME
	}
	
}
