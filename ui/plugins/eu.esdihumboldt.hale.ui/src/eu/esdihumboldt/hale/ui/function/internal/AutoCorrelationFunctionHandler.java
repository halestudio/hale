/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.function.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Shows the {@link AutoCorrelationFunctionWizard}
 * 
 * @author Yasmina Kammeyer
 */
public class AutoCorrelationFunctionHandler extends AbstractHandler {

	/**
	 * 
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		AutoCorrelationFunctionWizard wizard = new AutoCorrelationFunctionWizard();
		Shell shell = Display.getCurrent().getActiveShell();
		WizardDialog dialog = new WizardDialog(shell, wizard);

		if (dialog.open() == Window.OK) {
			// return ???;
		}

		return null;
	}

}
