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

package eu.esdihumboldt.hale.ui.util.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;

/**
 * Shows a wizard
 * 
 * @author Simon Templer
 */
public abstract class AbstractWizardHandler extends AbstractHandler {

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWizard wizard = createWizard();
		Shell shell = Display.getCurrent().getActiveShell();
		HaleWizardDialog dialog = new HaleWizardDialog(shell, wizard);
		dialog.open();

		return null;
	}

	/**
	 * Create the wizard
	 * 
	 * @return the wizard instance
	 */
	protected abstract IWizard createWizard();

}
