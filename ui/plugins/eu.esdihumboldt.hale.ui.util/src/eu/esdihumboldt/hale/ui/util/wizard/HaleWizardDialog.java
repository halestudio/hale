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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.util.wizard;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/**
 * Wizard dialog which adds supports custom title images from
 * {@link TitleImageWizard}s.
 * 
 * @author Simon Templer
 */
public class HaleWizardDialog extends WizardDialog {

	/**
	 * @see WizardDialog#WizardDialog(Shell, IWizard)
	 */
	public HaleWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}

	/**
	 * @see WizardDialog#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		if (getWizard() instanceof TitleImageWizard) {
			TitleImageWizard tiw = (TitleImageWizard) getWizard();
			Image titleImage = tiw.getTitleImage();
			if (titleImage != null) {
				newShell.setImage(titleImage);
			}
		}
	}

}
