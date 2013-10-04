/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.templates.contribution;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.widgets.Menu;

import eu.esdihumboldt.hale.ui.templates.extension.ProjectTemplate;
import eu.esdihumboldt.hale.ui.templates.extension.ProjectTemplateExtension;

/**
 * Contribution with actions to load project templates.
 * 
 * @author Simon Templer
 */
public class TemplatesContribution extends ContributionItem {

	@Override
	public void fill(Menu menu, int index) {
		for (ProjectTemplate template : ProjectTemplateExtension.getInstance().getElements()) {
			IAction action = new LoadTemplateAction(template);
			IContributionItem item = new ActionContributionItem(action);
			item.fill(menu, index++);
		}
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

}
