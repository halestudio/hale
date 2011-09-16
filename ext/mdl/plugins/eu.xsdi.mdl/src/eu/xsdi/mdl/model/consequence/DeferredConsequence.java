/*
 * LICENSE: This program is being made available under the LGPL 3.0 license.
 * For more information on the license, please read the following:
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * For additional information on the Model behind Mismatches, please refer to
 * the following publication(s):
 * Thorsten Reitz (2010): A Mismatch Description Language for Conceptual Schema 
 * Mapping and Its Cartographic Representation, Geographic Information Science,
 * http://www.springerlink.com/content/um2082120r51232u/
 */
package eu.xsdi.mdl.model.consequence;

import java.util.List;

import org.opengis.feature.Feature;

import eu.xsdi.mdl.model.Consequence;

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
