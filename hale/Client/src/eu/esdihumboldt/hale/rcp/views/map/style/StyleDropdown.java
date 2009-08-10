/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.map.style;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.rcp.HALEActivator;

/**
 * Drop-down action for style editing
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class StyleDropdown extends Action implements IMenuCreator {
	
	private final IContributionItem source;
	private final IContributionItem target;
	
	private Menu menu;
	
	/**
	 * Creates a style drop-down
	 */
	public StyleDropdown() {
		super("Styles", Action.AS_DROP_DOWN_MENU);
		
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				HALEActivator.PLUGIN_ID, "/icons/styles.gif"));
		
		source = new ActionContributionItem(new DatasetStyleDropdown(DatasetType.reference));
		target = new ActionContributionItem(new DatasetStyleDropdown(DatasetType.transformed));
		
		setMenuCreator(this);
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
	public void fillMenu(Menu menu) {
		source.fill(menu, 0);
		target.fill(menu, 1);
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
