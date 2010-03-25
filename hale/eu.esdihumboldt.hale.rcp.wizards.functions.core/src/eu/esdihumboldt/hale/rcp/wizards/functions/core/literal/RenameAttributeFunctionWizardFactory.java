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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.literal;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.corefunctions.RenameAttributeFunction;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory;

/**
 * Factory for the {@link RenameAttributeWizard}.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @since 2.0.0.M2
 */
public class RenameAttributeFunctionWizardFactory implements
		FunctionWizardFactory {

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory#createWizard(eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo)
	 */
	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {
		return new RenameAttributeWizard(selection);
	}

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory#supports(eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo)
	 */
	@Override
	public boolean supports(AlignmentInfo selection) {
		if (selection.getSourceItemCount() == 1 &&
				(selection.getTargetItemCount() == 1
						|| selection.getTargetItemCount() == 2)) {
			SchemaItem source = selection.getFirstSourceItem();
			SchemaItem target = selection.getFirstTargetItem();
			SchemaItem targetNested = null;
			if (selection.getTargetItemCount() == 2) {
				for (SchemaItem si : selection.getTargetItems()) {
					if (!si.equals(target)) {
						targetNested = si;
						break;
					}
				}
			}
			
			ICell cell = selection.getAlignment(source, target);
			
			if (cell != null) {
				// only allow editing matching transformation
				try {
					return cell.getEntity1().getTransformation().getService().getLocation().equals(
							RenameAttributeFunction.class.getName());
				} catch (NullPointerException e) {
					return false;
				}
			}
			else {
				if (source.isAttribute() && target.isAttribute()) {
					if (targetNested != null && !targetNested.isAttribute()) {
						return false;
					}
					return true;
				}
			}
		}
		
		return false;
	}

}
