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

import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Action that removes a custom function.
 * 
 * @author Simon Templer
 */
public class RemoveCustomFunctionAction extends Action {

	private final CustomPropertyFunction customFunction;
	private final AlignmentService alignmentService;

	/**
	 * Create an action to remove a custom function.
	 * 
	 * @param customFunction the custom function to remove
	 * @param alignmentService the alignment service
	 */
	public RemoveCustomFunctionAction(CustomPropertyFunction customFunction,
			AlignmentService alignmentService) {
		super(customFunction.getDescriptor().getDisplayName(), IAction.AS_PUSH_BUTTON);
		this.customFunction = customFunction;
		this.alignmentService = alignmentService;
	}

	@Override
	public void run() {
		alignmentService.removeCustomPropertyFunction(customFunction.getDescriptor().getId());
	}

}
