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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * The view activator ensures that the view containing Swing components is
 * activated when the mouse is pressed on one of the components. The components
 * have to be added to the activator through {@link #addComponent(JComponent)}.
 * 
 * @author Simon Templer
 */
public class ViewActivator {

	private final String viewId;
	private final MouseAdapter mouseAdapter;

	/**
	 * Create a view activator.
	 * 
	 * @param viewSite the site associated to the view
	 */
	public ViewActivator(IViewSite viewSite) {
		viewId = viewSite.getId();

		mouseAdapter = new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				// activate the view if not yet active
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						if (!viewId.equals(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getActivePage().getActivePart().getSite().getId())) {
							try {
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
										.showView(viewId);
							} catch (PartInitException e) {
								// ignore
							}
						}
					}
				});
			}
		};
	}

	/**
	 * Add a component where a mouse press shall ensure that the view is
	 * activated.
	 * 
	 * @param c the component to added the activator's mouse listener to
	 */
	public void addComponent(JComponent c) {
		c.addMouseListener(mouseAdapter);
	}

}
