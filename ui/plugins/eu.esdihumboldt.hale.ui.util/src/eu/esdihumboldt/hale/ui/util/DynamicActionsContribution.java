/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
