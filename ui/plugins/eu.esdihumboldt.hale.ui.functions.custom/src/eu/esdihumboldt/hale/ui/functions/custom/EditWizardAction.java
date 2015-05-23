/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.functions.custom;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunction;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;

/**
 * Action for showing a wizard to edit a custom function.
 * 
 * @author Simon Templer
 */
public class EditWizardAction extends Action {

	private final AlignmentService alignmentService;
	private final DefaultCustomPropertyFunction customFunction;

	/**
	 * @param customFunction the custom function to edit
	 * @param alignmentService the alignment service
	 */
	public EditWizardAction(DefaultCustomPropertyFunction customFunction,
			AlignmentService alignmentService) {
		super(customFunction.getName(), IAction.AS_PUSH_BUTTON);
		this.customFunction = customFunction;
		this.alignmentService = alignmentService;
	}

	@Override
	public void run() {
		CustomPropertyFunctionWizard wizard = new CustomPropertyFunctionWizard(customFunction);
		wizard.init();

		Shell shell = Display.getCurrent().getActiveShell();
		HaleWizardDialog dialog = new HaleWizardDialog(shell, wizard);
		if (dialog.open() == HaleWizardDialog.OK) {
			alignmentService.addCustomPropertyFunction(wizard.getResultFunction());
		}
	}

}
