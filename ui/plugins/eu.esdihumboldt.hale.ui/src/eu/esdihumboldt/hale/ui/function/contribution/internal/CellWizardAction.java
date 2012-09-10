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

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.function.contribution.CellFunctionContribution;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Action for creating a function wizard based on a {@link Cell}
 * 
 * @author Simon Templer
 */
public class CellWizardAction extends AbstractWizardAction<CellFunctionContribution> {

	private final Cell originalCell;

	/**
	 * Constructor
	 * 
	 * @param functionContribution the parent contribution
	 * @param originalCell the original cell
	 * @param descriptor the function wizard descriptor
	 * @param alignmentService the alignment service
	 */
	public CellWizardAction(CellFunctionContribution functionContribution, Cell originalCell,
			FunctionWizardDescriptor<?> descriptor, AlignmentService alignmentService) {
		super(functionContribution, descriptor, alignmentService);

		this.originalCell = originalCell;
	}

	/**
	 * @see AbstractWizardAction#createWizard()
	 */
	@Override
	protected FunctionWizard createWizard() {
		return descriptor.createEditWizard(functionContribution.getCell());
	}

	/**
	 * @see AbstractWizardAction#handleResult(MutableCell)
	 */
	@Override
	protected void handleResult(MutableCell cell) {
		// remove the original cell
		// and add the new cell
		alignmentService.replaceCell(originalCell, cell);
	}

}
