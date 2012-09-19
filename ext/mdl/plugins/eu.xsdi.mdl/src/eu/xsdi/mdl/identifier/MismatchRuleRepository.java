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
