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

/**
 * A {@link DeferredConsequence} is one whose {@link Measurement} can only be 
 * calculated at execution time, based on the results of the instance 
 * translation.
 * 
 * The calculation rule used depends on the Mismatch type, and e.g. for 
 * completeness usually evaluates the size of two sets.
 * 
 * The following is an example rule for completeness:
 * <pre>double: |Entity1.PropertyA != null | / |Entity2.PropertyB != null|</pre>
 * 
 * Rules can also be used to get boolean results, such as in this case, which 
 * evaluates existence of a certain element:
 * <pre>boolean: forAll(Entity1.PropertyA != null)</pre>
 * forAll rules are evaluated once based on the final data set, and result set 
 * size must be identical to input set size for the result to be true.
 * <br/>
 * TODO: A full grammar is to follow.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class DeferredConsequence 
	extends Consequence {
	
	private String calculationRule;

	public DeferredConsequence(String calculationRule) {
		super();
		this.calculationRule = calculationRule;
	}

	public String getCalculationRule() {
		return calculationRule;
	}

	public void setCalculationRule(String calculationRule) {
		this.calculationRule = calculationRule;
	}

}
