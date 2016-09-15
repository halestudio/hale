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

import java.util.Collection;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Menu contribution for editing custom functions.
 * 
 * @author Simon Templer
 */
public class EditCustomFunctionContribution extends ContributionItem {

	@Override
	public void fill(Menu menu, int index) {
		AlignmentService alignmentService = PlatformUI.getWorkbench()
				.getService(AlignmentService.class);

		Collection<CustomPropertyFunction> functions = alignmentService.getAlignment()
				.getCustomPropertyFunctions().values();
		for (CustomPropertyFunction function : functions) {
			if (function instanceof DefaultCustomPropertyFunction) {
				// XXX currently only these functions editable
				DefaultCustomPropertyFunction cf = (DefaultCustomPropertyFunction) function;

				EditWizardAction action = new EditWizardAction(cf, alignmentService);
				IContributionItem item = new ActionContributionItem(action);
				item.fill(menu, index++);
			}
		}

		// TODO add base alignment functions (disabled, with separator)?
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

}
