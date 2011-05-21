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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.geometric;

import eu.esdihumboldt.cst.corefunctions.NetworkExpansionFunction;
import eu.esdihumboldt.hale.ui.model.functions.AlignmentInfo;
import eu.esdihumboldt.hale.ui.model.functions.FunctionWizard;
import eu.esdihumboldt.hale.ui.model.functions.FunctionWizardFactory;
import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.hale.ui.model.schema.TreeObject.TreeObjectType;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Factory for {@link NetworkExpansionFunctionWizard}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class NetworkExpansionFunctionWizardFactory implements
		FunctionWizardFactory {

	/**
	 * @see FunctionWizardFactory#createWizard(AlignmentInfo)
	 */
	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {
		return new NetworkExpansionFunctionWizard(selection);
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
			
			if (source == null || target == null) {
				return false;
			}
			
			if (!source.getType().equals(TreeObjectType.GEOMETRIC_ATTRIBUTE) 
					 || !target.getType().equals(TreeObjectType.GEOMETRIC_ATTRIBUTE)) {
				return false;
			}
			
			ICell cell = selection.getAlignment(
					source, target);
			
			if (cell != null) {
				// only allow editing matching transformation
				try {
					return cell.getEntity1().getTransformation().getService().getLocation().equals(
							NetworkExpansionFunction.class.getName());
				} catch (NullPointerException e) {
					return false;
				}
			}
			else if (source.isAttribute() && target.isAttribute()) {
				//TODO more sophisticated check
				return true;
			}
		}
		
		return false;
	}

}
