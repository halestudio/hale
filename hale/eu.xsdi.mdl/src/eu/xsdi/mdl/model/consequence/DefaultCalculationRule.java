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

import eu.xsdi.mdl.model.consequence.DeferredConsequence.CalculationRule;

/**
 * This {@link CalculationRule} implementation provides a generic solution for 
 * some common {@link DataQualityElement} assessments, such as completeness 
 * calculations.
 * 
 * A complete rule has the following parts:
 * <pre> returnType : function(element*)* </pre>
 * 
 * An expression is usually built around elements of the original and 
 * transformed data sets to evaluate. These are identified by their local name. 
 * An element like this
 * <pre> TypenameA.PropertynameA1 </pre>
 * indicates the property with the local name PropertynameA1 in the type with 
 * the local name TypenameA is referenced. To test the element against any 
 * condition, you can use these operators:
 * <pre> TypenameA.PropertynameA1 == 1.0 (tests for equality, works on all operand types)
 * TypenameA.PropertynameA1 != 1.0 (tests for non-equality, works on all operand types)
 * TypenameA.PropertynameA1 < 1.0
 * TypenameA.PropertynameA1 > 1.0
 * TypenameA.PropertynameA1 <= 1.0
 * TypenameA.PropertynameA1 >= 1.0 </pre> 
 * Strings are indicated using ticks ('). You can also test for null. Finally, 
 * expressions can be combined using key word AND, OR and XOR. Note that each 
 * expression has to be in round brackets:
 * <pre> (TypenameA.PropertynameA1 != null) AND (TypenameA.PropertynameA1 != null)</pre>
 * 
 * Finally, you can also use some predefined functions in your expression:
 * <pre> abs(all(TypenameA.PropertynameA1 != null)) </pre>
 * This will return the size of the set of all the TypenameA elements where 
 * PropertynameA1 is not null. You can use this to e.g. determine ratios and 
 * get a complete rule:
 * <pre> number: abs(all(TypenameA.PropertynameA1 != null)) / abs(all(TypenameB.PropertynameB1 != null)) </pre>
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class DefaultCalculationRule 
	implements CalculationRule<Feature> {
	
	private String expression;

	public DefaultCalculationRule(String expression) {
		super();
		this.expression = expression;
	}

	@Override
	public List<DataQualityElement> evaluate(
			List<? extends Feature> originalObjects,
			List<? extends Feature> transformedObjects) {
		if (expressionIsValid()) {
			System.out.println(this.expression);
		}
		
		return null;
	}

	private boolean expressionIsValid() {
		// FIXME
		return true;
	}


}
