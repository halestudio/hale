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

package eu.xsdi.mdl.identifier;

import java.util.ArrayList;
import java.util.List;

import eu.xsdi.mdl.model.Reason.EntityCharacteristic;
import eu.xsdi.mdl.model.reason.ReasonCondition;
import eu.xsdi.mdl.model.reason.ReasonRule;
import eu.xsdi.mdl.model.reason.ReasonSet;

/**
 * TODO Add Type comment
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @since 2.0.0.M2
 */
public class MismatchRuleRepository {

	public static List<ReasonRule> getCellbasedRules() {

		ReasonSet rs1 = new ReasonSet();
		List<ReasonCondition> conditions1 = new ArrayList<ReasonCondition>();
		conditions1.add(new ReasonCondition(
				null, // to be applied to all elements
				null, // no value filter
				EntityCharacteristic.AttributeCardinalityConstraint) // selected Characteristic for this Mismatch
		);
		rs1.setConditions(conditions1);
		
		ReasonSet rs2 = new ReasonSet();
		List<ReasonCondition> conditions2 = new ArrayList<ReasonCondition>();
		conditions2.add(new ReasonCondition(
				null, // to be applied to all elements
				null, // no value filter
				EntityCharacteristic.AttributeCardinalityConstraint) // selected Characteristic for this Mismatch
		);
		rs2.setConditions(conditions2);
		
		ReasonRule rr1 = new ReasonRule(rs1, rs2);
		
		return null;
	}

}
