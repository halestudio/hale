/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.functions.core.inspire;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.corefunctions.inspire.IdentifierFunction;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory;

/**
 * This is the {@link FunctionWizardFactory} for the {@link IdentifierFunction}.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class IdentifierFunctionWizardFactory implements FunctionWizardFactory {

	/**
	 * @see eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory#createWizard(eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo)
	 */
	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {
		return new IdentifierFunctionWizard(selection);
	}

	/**
	 * @see eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory#supports(eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo)
	 */
	@Override
	public boolean supports(AlignmentInfo selection) {
		// must be at least one source item and exactly one target item
		if (selection.getSourceItemCount() != 1 || selection.getTargetItemCount() != 1) {
			return false;
		}
		
		// target item must be a property and be an INSPIRE identifier
		SchemaItem target = selection.getFirstTargetItem();
		if (!target.isAttribute()) {
			return false;
		}
		if (!target.getPropertyType().getName().getLocalPart().equals("IdentifierPropertyType")) {
			return false;
		}
		
		// source items must be properties
		SchemaItem source = selection.getFirstSourceItem();
		if (!source.isAttribute()) {
			return false;
		}
		
		ICell cell = selection.getAlignment(selection.getSourceItems(),
				selection.getTargetItems());
		if (cell != null) {
			// only allow editing matching transformation
			try {
				return cell.getEntity1().getTransformation().getService().getLocation().equals(
						IdentifierFunction.class.getName());
			} catch (NullPointerException e) {
				return false;
			}
		}
		
		return true;
	}

}
