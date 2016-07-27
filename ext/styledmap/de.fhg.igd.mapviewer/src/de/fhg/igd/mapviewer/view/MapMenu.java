/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import de.fhg.igd.mapviewer.view.cache.TileCacheContribution;
import de.fhg.igd.mapviewer.view.overlay.MapPainterContribution;
import de.fhg.igd.mapviewer.view.overlay.TileOverlayContribution;
import de.fhg.igd.mapviewer.view.server.MapServerContribution;

/**
 * Menu containing map related actions
 * 
 * @author Simon Templer
 */
public class MapMenu extends ContributionItem {

	private final List<IContributionItem> overlayMenu;

	private final IContributionItem cacheMenu;

	private final Separator separator;

	private final IContributionItem serverMenu;

	/**
	 * Constructor
	 */
	public MapMenu() {
		super();

		cacheMenu = new TileCacheContribution();

		overlayMenu = new ArrayList<IContributionItem>();
		overlayMenu.add(new TileOverlayContribution());
		overlayMenu.add(new MapPainterContribution());

		separator = new Separator();

		serverMenu = new MapServerContribution();
	}

	/**
	 * @see ContributionItem#fill(Menu, int)
	 */
	@Override
	public void fill(Menu menu, int index) {
		serverMenu.fill(menu, index);

		index = menu.getItemCount();

		separator.fill(menu, index++);

		MenuItem item = new MenuItem(menu, SWT.CASCADE, index++);
		item.setText(Messages.MapMenu_0);
		Menu submenu = new Menu(item);
		item.setMenu(submenu);

		for (IContributionItem con : overlayMenu) {
			con.fill(submenu, 0);
		}

		item = new MenuItem(menu, SWT.CASCADE, index++);
		item.setText(Messages.MapMenu_1);
		submenu = new Menu(item);
		item.setMenu(submenu);

		cacheMenu.fill(submenu, 0);
	}

	/**
	 * @see ContributionItem#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}

}
