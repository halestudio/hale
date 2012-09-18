/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
