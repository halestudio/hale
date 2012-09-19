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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.function.contribution.AbstractFunctionWizardContribution;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Action for creating a function wizard
 * 
 * @param <T> the parent contribution type
 * 
 * @author Simon Templer
 */
public abstract class AbstractWizardAction<T extends AbstractFunctionWizardContribution> extends
		Action {

	/**
	 * The parent function wizard contribution
	 */
	protected final T functionContribution;

	/**
	 * The alignment service
	 */
	protected final AlignmentService alignmentService;

	/**
	 * The function wizard descriptor
	 */
	protected final FunctionWizardDescriptor<?> descriptor;

	/**
	 * Constructor
	 * 
	 * @param functionContribution the parent contribution
	 * @param descriptor the function wizard descriptor
	 * @param alignmentService the alignment service
	 */
	public AbstractWizardAction(T functionContribution, FunctionWizardDescriptor<?> descriptor,
			AlignmentService alignmentService) {
		super(descriptor.getDisplayName(), IAction.AS_PUSH_BUTTON);
		this.functionContribution = functionContribution;

		this.descriptor = descriptor;
		this.alignmentService = alignmentService;

		setImageDescriptor(ImageDescriptor.createFromURL(descriptor.getIconURL()));

//		if (selectionService != null) {
//			selectionService.addSelectionListener(this);
//		}

		updateState();
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		FunctionWizard wizard = createWizard();

		if (wizard != null) {
			// initialize the wizard
			wizard.init();

			WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);

			if (dialog.open() == WizardDialog.OK) {
				MutableCell cell = wizard.getResult();
				handleResult(cell);
			}
		}
	}

	/**
	 * Handle the wizard result
	 * 
	 * @param cell the result cell
	 */
	protected abstract void handleResult(MutableCell cell);

	/**
	 * Create the function wizard
	 * 
	 * @return the function wizard
	 */
	protected abstract FunctionWizard createWizard();

//	/**
//	 * @see ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
//	 */
//	@Override
//	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
//		updateState();
//	}

	/**
	 * Update the action state
	 */
	protected void updateState() {
		setEnabled(isActive());
	}

	/**
	 * Get if the wizard action shall be currently active
	 * 
	 * @return if the wizard action shall be currently active
	 */
	public boolean isActive() {
		return functionContribution.isActive(descriptor);
	}

}
