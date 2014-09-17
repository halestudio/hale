/*
 * Copyright (c) 2014 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.esdihumboldt.hale.ui.views.styledmap.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.fhg.igd.mapviewer.view.MapView;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.ui.views.styledmap.StyledMapPerspective;
import eu.esdihumboldt.hale.ui.views.styledmap.StyledMapView;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.IconPainterLayoutContribution;

/**
 * Zooms to all instances present in the map.
 * 
 * @author Simon Templer
 */
public class ShowLayoutMenuHandler extends AbstractHandler {

	private static final ALogger log = ALoggerFactory.getLogger(ShowLayoutMenuHandler.class);

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IViewPart viewPart = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
				.findView(StyledMapView.ID);

		if (viewPart instanceof MapView) {
			// view visible - show layout menu
			final MenuManager manager = new MenuManager();
			manager.setRemoveAllWhenShown(true);
			final IconPainterLayoutContribution contribution = new IconPainterLayoutContribution();
			manager.addMenuListener(new IMenuListener() {

				@Override
				public void menuAboutToShow(IMenuManager manager) {
					// populate context menu
					manager.add(contribution);
				}

			});
			Shell shell = HandlerUtil.getActiveShell(event);

			final Menu menu = manager.createContextMenu(shell);

			// determine location
			Point cursorLocation = Display.getCurrent().getCursorLocation();

			// default to cursor location
			Point location = cursorLocation;

			// try to determine from control
			Control cursorControl = Display.getCurrent().getCursorControl();
			if (cursorControl != null) {
				if (cursorControl instanceof ToolBar) {
					ToolBar bar = (ToolBar) cursorControl;
					ToolItem item = bar.getItem(bar.toControl(cursorLocation));
					if (item != null) {
						Rectangle bounds = item.getBounds();
						location = bar.toDisplay(bounds.x, bounds.y + bounds.height);
					}
				}
				else {
					// show below control
					location = cursorControl.toDisplay(0, cursorControl.getSize().y);
				}
			}

			menu.setLocation(location);

			menu.addMenuListener(new MenuListener() {

				@Override
				public void menuShown(MenuEvent e) {
					// do nothing
				}

				@Override
				public void menuHidden(MenuEvent e) {
					Display.getCurrent().asyncExec(new Runnable() {

						@Override
						public void run() {
							/*
							 * Dispose everything as it is used only once. Done
							 * asynchronously as otherwise we interfere with the
							 * menu click handling.
							 */
							manager.dispose();
							contribution.dispose();
							menu.dispose();
						}
					});
				}
			});

			// show menu
			menu.setVisible(true);
		}
		else {
			// view not visible - just show map perspective
			try {
				PlatformUI.getWorkbench().showPerspective(StyledMapPerspective.ID,
						HandlerUtil.getActiveWorkbenchWindow(event));
			} catch (WorkbenchException e) {
				log.error("Could not open map perspective", e);
			}
		}

		return null;
	}
}
