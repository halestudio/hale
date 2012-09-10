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

package eu.esdihumboldt.hale.ui.function.contribution;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.function.contribution.internal.AbstractWizardAction;
import eu.esdihumboldt.hale.ui.function.contribution.internal.CellWizardAction;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Function wizard contribution based on a {@link Cell}
 * 
 * @author Simon Templer
 */
public class CellFunctionContribution extends AbstractFunctionWizardContribution {

	private final Cell cell;

	/**
	 * Constructor
	 * 
	 * @param cell the cell
	 */
	public CellFunctionContribution(Cell cell) {
		super();

		this.cell = cell;
	}

	/**
	 * @see AbstractFunctionWizardContribution#createWizardAction(FunctionWizardDescriptor,
	 *      AlignmentService)
	 */
	@Override
	protected AbstractWizardAction<?> createWizardAction(FunctionWizardDescriptor<?> descriptor,
			AlignmentService alignmentService) {
		return new CellWizardAction(this, cell, descriptor, alignmentService);
	}

	/**
	 * @see AbstractFunctionWizardContribution#isActive(FunctionWizardDescriptor)
	 */
	@Override
	public boolean isActive(FunctionWizardDescriptor<?> descriptor) {
		// function id must match cell transformation
		return descriptor.getFunctionId().equals(getCell().getTransformationIdentifier());
	}

	/**
	 * Get the cell
	 * 
	 * @return the cell
	 */
	public Cell getCell() {
		return cell;
	}

}
