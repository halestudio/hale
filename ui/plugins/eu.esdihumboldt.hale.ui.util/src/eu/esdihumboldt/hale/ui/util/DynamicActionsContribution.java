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

package eu.esdihumboldt.hale.ui.util;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;

/**
 * Dynamic contribution item based on a list of actions.
 * 
 * @author Simon Templer
 */
public abstract class DynamicActionsContribution extends ContributionItem {

	/**
	 * Get the actions that are to be displayed in the contribution. A
	 * <code>null</code> action represents a separator.
	 * 
	 * @return the actions
	 */
	protected abstract Iterable<IAction> getActions();

	/**
	 * @see ContributionItem#fill(Composite)
	 */
	@Override
	public void fill(Composite parent) {
		for (IAction action : getActions()) {
			if (action == null) {
				new Separator().fill(parent);
			}
			else {
				IContributionItem item = new ActionContributionItem(action);
				item.fill(parent);
			}
		}
	}

	/**
	 * @see ContributionItem#fill(Menu, int)
	 */
	@Override
	public void fill(Menu menu, int index) {
		for (IAction action : getActions()) {
			if (action == null) {
				new Separator().fill(menu, index++);
			}
			else {
				IContributionItem item = new ActionContributionItem(action);
				item.fill(menu, index++);
			}
		}
	}

	/**
	 * @see ContributionItem#fill(ToolBar, int)
	 */
	@Override
	public void fill(ToolBar parent, int index) {
		for (IAction action : getActions()) {
			if (action == null) {
				new Separator().fill(parent, index++);
			}
			else {
				IContributionItem item = new ActionContributionItem(action);
				item.fill(parent, index++);
			}
		}
	}

	/**
	 * @see ContributionItem#fill(CoolBar, int)
	 */
	@Override
	public void fill(CoolBar parent, int index) {
		for (IAction action : getActions()) {
			if (action == null) {
				new Separator().fill(parent, index++);
			}
			else {
				IContributionItem item = new ActionContributionItem(action);
				item.fill(parent, index++);
			}
		}
	}

	/**
	 * @see ContributionItem#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}

}
