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

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.util.handler.AbstractWizardHandler;

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
public class NewCustomPropertyFunctionHandler
		extends AbstractWizardHandler<CustomPropertyFunctionWizard> {

	@Override
	protected CustomPropertyFunctionWizard createWizard() {
		CustomPropertyFunctionWizard wiz = new CustomPropertyFunctionWizard();
		wiz.init();
		return wiz;
	}

	@Override
	protected void onComplete(CustomPropertyFunctionWizard wizard) {
		AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
		as.addCustomPropertyFunction(wizard.getResultFunction());
	}

}
