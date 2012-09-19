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
package eu.esdihumboldt.hale.ui.util.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * Drop-down action
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class DropdownAction extends Action implements IMenuCreator {

	private final List<IContributionItem> items = new ArrayList<IContributionItem>();

	private Menu menu;

	/**
	 * Creates a drop-down action
	 * 
	 * @param name the action name
	 */
	public DropdownAction(String name) {
		super(name, Action.AS_DROP_DOWN_MENU);

		setMenuCreator(this);
	}

	/**
	 * Adds an item to the drop-down menu
	 * 
	 * @param item the item to add
	 */
	public void addItem(IContributionItem item) {
		items.add(item);
	}

	/**
	 * @see IMenuCreator#dispose()
	 */
	@Override
	public void dispose() {
		if (menu != null) {
			menu.dispose();
		}
	}

	/**
	 * @see IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
	 */
	@Override
	public Menu getMenu(Control parent) {
		dispose();

		menu = new Menu(parent);
		fillMenu(menu);

		return menu;
	}

	/**
	 * Fill a menu
	 * 
	 * @param menu the menu to fill
	 */
	protected void fillMenu(Menu menu) {
		for (int i = 0; i < items.size(); i++) {
			items.get(i).fill(menu, i);
		}
	}

	/**
	 * @see IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
	 */
	@Override
	public Menu getMenu(Menu parent) {
		dispose();

		menu = new Menu(parent);
		fillMenu(menu);

		return menu;
	}

}
