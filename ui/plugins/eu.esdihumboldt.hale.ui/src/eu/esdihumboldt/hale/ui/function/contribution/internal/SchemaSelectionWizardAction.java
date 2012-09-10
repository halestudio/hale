/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.function.contribution.internal;

import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.function.contribution.AbstractFunctionWizardContribution;
import eu.esdihumboldt.hale.ui.function.contribution.SchemaSelectionFunctionContribution;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Action for creating a function wizard based on a {@link SchemaSelection}
 * 
 * @author Simon Templer
 */
public class SchemaSelectionWizardAction extends
		AbstractWizardAction<SchemaSelectionFunctionContribution> {

	/**
	 * @see AbstractWizardAction#AbstractWizardAction(AbstractFunctionWizardContribution,
	 *      FunctionWizardDescriptor, AlignmentService)
	 */
	public SchemaSelectionWizardAction(SchemaSelectionFunctionContribution functionContribution,
			FunctionWizardDescriptor<?> descriptor, AlignmentService alignmentService) {
		super(functionContribution, descriptor, alignmentService);
	}

	/**
	 * @see AbstractWizardAction#createWizard()
	 */
	@Override
	protected FunctionWizard createWizard() {
		return descriptor.createNewWizard(functionContribution.getSelection());
	}

	/**
	 * @see AbstractWizardAction#handleResult(MutableCell)
	 */
	@Override
	protected void handleResult(MutableCell cell) {
		// add the new cell
		alignmentService.addCell(cell);
	}

}
