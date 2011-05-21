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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.date;

import java.util.Date;

import eu.esdihumboldt.cst.corefunctions.DateExtractionFunction;
import eu.esdihumboldt.hale.ui.model.functions.AlignmentInfo;
import eu.esdihumboldt.hale.ui.model.functions.FunctionWizard;
import eu.esdihumboldt.hale.ui.model.functions.FunctionWizardFactory;
import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * This is the {@link FunctionWizardFactory} for the {@link DateExtractionFunction}.
 * 
 * @author Ulrich Schaeffler 
 * @partner 14 / TUM
 */
public class DateExtractionFunctionWizardFactory implements
		FunctionWizardFactory {

	/**
	 * @see eu.esdihumboldt.hale.ui.model.functions.FunctionWizardFactory#createWizard(eu.esdihumboldt.hale.ui.model.functions.AlignmentInfo)
	 */
	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {
		return new DateExtractionFunctionWizard(selection);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.model.functions.FunctionWizardFactory#supports(eu.esdihumboldt.hale.ui.model.functions.AlignmentInfo)
	 */
	@Override
	public boolean supports(AlignmentInfo selection) {
		// must be at least one source item and exactly one target item
		if (selection.getSourceItemCount() != 1 || selection.getTargetItemCount() != 1) {
			return false;
		}
		
		// target item must be a property
		SchemaItem target = selection.getFirstTargetItem();
		if (!target.isAttribute()) {
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
						DateExtractionFunction.class.getName());
			} catch (NullPointerException e) {
				return false;
			}
		}
		else {
			// new cell
			
			// source item must be a string
			if (!String.class.isAssignableFrom(source.getPropertyType().getBinding())) {
				return false;
			}
			
			// target must be a string or a date
			Class<?> targetBinding = target.getPropertyType().getBinding();
			if (!String.class.isAssignableFrom(targetBinding)
					&& !Date.class.isAssignableFrom(targetBinding)) {
				return false;
			}
		}
		
		return true;
	}

}
