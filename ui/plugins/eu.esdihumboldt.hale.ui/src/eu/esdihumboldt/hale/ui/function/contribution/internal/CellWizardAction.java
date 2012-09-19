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
