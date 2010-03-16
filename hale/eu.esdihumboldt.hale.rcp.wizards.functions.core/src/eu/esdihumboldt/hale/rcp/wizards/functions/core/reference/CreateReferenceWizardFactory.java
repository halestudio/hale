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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.reference;

import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory;

/**
 * @author Thorsten Reitz
 * @version $Id$
 */
public class CreateReferenceWizardFactory 
	implements FunctionWizardFactory {

	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {
		return new CreateReferenceWizard(selection);
	}

	@Override
	public boolean supports(AlignmentInfo selection) {
		if (selection.getSourceItemCount() != 1 || selection.getTargetItemCount() != 1) {
			return false;
		}
		SchemaItem target = selection.getFirstTargetItem();
		SchemaItem source = selection.getFirstSourceItem();
		if (!target.isAttribute() && !source.isAttribute()) {
			return false;
		}
		
		// target item must be a ReferenceType
		if (!target.getPropertyType().getName().getLocalPart().equals("ReferenceType")) {
			return false;
		}
		
		// source item must be alphanumeric
		if (source.getType().equals(TreeObjectType.NUMERIC_ATTRIBUTE) 
						|| source.getType().equals(TreeObjectType.STRING_ATTRIBUTE)) {
			return true;
		}
		
		return false;
	}

}
