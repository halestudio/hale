/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.quality.mdl;

import java.util.List;

import org.opengis.feature.Feature;

/**
 * A {@link DeferredConsequence} is one whose {@link Measurement} can only be 
 * calculated at execution time, based on the results of the instance 
 * translation.
 * The calculation makes use of a {@link CalculationRule}.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class DeferredConsequence<T> 
	extends Consequence {
	
	private final CalculationRule<T> calculationRule;
	
	private List<? extends T> originalObjects;
	
	private List<? extends T> transformedObjects;
	
	public DeferredConsequence(CalculationRule<T> calculationRule) {
		super();
		this.calculationRule = calculationRule;
	}

	/**
	 * @return the {@link CalculationRule} that has been set.
	 */
	public CalculationRule<?> getCalculationRule() {
		return calculationRule;
	}
	
	/**
	 * Sets the variables to use for the calculation, such as the sets of source
	 * and transformed features.
	 * @param originalObjects
	 * @param transformedObjects
	 */
	public void setCalculationVariables(
			List<? extends T> originalObjects, 
			List<? extends T> transformedObjects) {
		this.originalObjects = originalObjects;
		this.transformedObjects = transformedObjects;
	}
	
	@Override
	public List<DataQualityElement> getImpact() {
		return this.calculationRule.evaluate(
				originalObjects, transformedObjects);
	}

	@Override
	public void setImpact(List<DataQualityElement> impact) {
		throw new UnsupportedOperationException("You cannot set an Impact on" +
				" a DeferredConsequence.");
	}



	/**
	 * Types implementing this interface can be used to submit custom rules for
	 * the calculation of the values of {@link DataQualityElement}s of a 
	 * {@link Consequence}.
	 * 
	 * @author Thorsten Reitz
	 * @version $Id$ 
	 * @param <T> any type to base a quality impact calculation on; usually
	 * {@link Feature}s.
	 */
	public interface CalculationRule<T> {
		
		/**
		 * 
		 * @param originalObjects a {@link List} of objects as they appeared 
		 * before the transformation
		 * @param transformedObjects a corresponding {@link List} of objects as
		 * they appear after transformation
		 * @return
		 */
		public List<DataQualityElement> evaluate(
				List<? extends T> originalObjects, 
				List<? extends T> transformedObjects);
		
	}

}
