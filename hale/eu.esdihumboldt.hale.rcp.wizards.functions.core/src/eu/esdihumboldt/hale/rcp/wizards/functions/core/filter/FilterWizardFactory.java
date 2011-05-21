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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.filter;

import java.util.List;

import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.omwg.Restriction;
import eu.esdihumboldt.hale.ui.model.functions.AlignmentInfo;
import eu.esdihumboldt.hale.ui.model.functions.FunctionWizard;
import eu.esdihumboldt.hale.ui.model.functions.FunctionWizardFactory;
import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Factory for {@link FilterWizard}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class FilterWizardFactory implements FunctionWizardFactory {

	/**
	 * @see FunctionWizardFactory#createWizard(AlignmentInfo)
	 */
	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {
		return new FilterWizard(selection);
	}

	/**
	 * @see FunctionWizardFactory#supports(AlignmentInfo)
	 */
	@Override
	public boolean supports(AlignmentInfo selection) {
		if (selection.getSourceItemCount() == 1 &&
				selection.getTargetItemCount() == 1) {
			SchemaItem source = selection.getFirstSourceItem();
			SchemaItem target = selection.getFirstTargetItem();
			
			if (!source.isType() || !target.isType()) {
				// only types supported
				return false;
			}
			
			ICell cell = selection.getAlignment(source, target);
			
			if (cell == null) {
				return false;
			}
			
			// a filter must not be present, because parsing the filter expression is not implemented
			List<Restriction> restrictions;
			if (cell.getEntity1() instanceof FeatureClass) {
				restrictions = ((FeatureClass) cell.getEntity1()).getAttributeValueCondition();
			}
			else if (cell.getEntity1() instanceof Property) {
				restrictions = ((Property) cell.getEntity1()).getValueCondition();
			}
			else {
				restrictions = null;
			}
			
			if (restrictions == null || restrictions.isEmpty()) {
				return true;
			}
			else {
				// check for a filter restriction
				for (Restriction restriction : restrictions) {
					if (restriction.getCqlStr() != null && !restriction.getCqlStr().isEmpty()) {
						return false;
					}
				}
				
				return true;
			}
		}
		
		return false;
	}

}
