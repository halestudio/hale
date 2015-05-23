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

import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunction;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * TODO Type description
 * 
 * @author simon
 */
public class EditWizardAction extends Action {

	/**
	 * @param cf
	 * @param alignmentService
	 */
	public EditWizardAction(DefaultCustomPropertyFunction cf, AlignmentService alignmentService) {
		super(cf.getName(), IAction.AS_PUSH_BUTTON);
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
	}

}
