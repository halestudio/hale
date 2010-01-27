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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.inspire.geographicname;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.corefunctions.GenericMathFunction;
import eu.esdihumboldt.cst.corefunctions.inspire.GeographicalNameFunction;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory;

/**
 * This is the {@link FunctionWizardFactory} for the {@link GeographicalNameFunction}.
 *
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$ 
 */
public class GeographicNameFunctionWizardFactory implements
		FunctionWizardFactory {

	/**
	 * @see eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory#createWizard(eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo)
	 */
	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {
		
		return new GeographicNameFunctionWizard(selection);
	}

	/**
	 * @see eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory#supports(eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo)
	 */
	@Override
	public boolean supports(AlignmentInfo selection) {
		boolean supports = true;
		// must be at least one source item and exactly one target item
		if (selection.getSourceItemCount() < 1 || selection.getTargetItemCount() != 1) {
			supports = false;
		}
		
		// target item must be a property of the type geometric attribute
		//FIXME add check for the geographical name type.
		SchemaItem target = selection.getFirstTargetItem();
		if (!target.isAttribute() || !target.getType().equals(TreeObjectType.GEOGRAPHICAl_NAME_ATTRIBUTE)) {
			supports = false;
		}
		
		// source items must be properties of the type STRING_ATTRIBUTE
		for (SchemaItem source : selection.getSourceItems()) {
			if (!source.isAttribute() || !source.getType().equals(TreeObjectType.STRING_ATTRIBUTE)) {
				supports = false;
			}
		}
		
		ICell cell = selection.getAlignment(selection.getSourceItems(),
				selection.getTargetItems());
		if (cell != null) {
			// only allow editing matching transformation
			try {
				return cell.getEntity1().getTransformation().getService().getLocation().equals(
						GeographicalNameFunction.class.getName());
			} catch (NullPointerException e) {
				supports = false;
			}
		}
		
		return supports;
		
	}

}
