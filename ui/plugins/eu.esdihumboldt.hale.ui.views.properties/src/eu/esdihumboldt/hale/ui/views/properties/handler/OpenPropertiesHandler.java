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

package eu.esdihumboldt.hale.ui.views.properties.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.views.properties.PropertySheet;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Shows the properties view
 * 
 * @author Simon Templer
 */
public class OpenPropertiesHandler extends AbstractHandler {

	private static final ALogger log = ALoggerFactory.getLogger(OpenPropertiesHandler.class);

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		unpinAndOpenPropertiesView(HandlerUtil.getActiveWorkbenchWindow(event));
		return null;
	}

	/**
	 * Unpin and open the porperties view.
	 * 
	 * @param activeWindow the active workbench window
	 */
	public static void unpinAndOpenPropertiesView(IWorkbenchWindow activeWindow) {
		try {
			// unpin the property sheet if possible
			IViewReference ref = activeWindow.getActivePage().findViewReference(
					IPageLayout.ID_PROP_SHEET);
			if (ref != null) {
				IViewPart part = ref.getView(false);
				if (part instanceof PropertySheet) {
					PropertySheet sheet = (PropertySheet) part;
					if (sheet.isPinned()) {
						sheet.setPinned(false);

						IWorkbenchPart activePart = activeWindow.getActivePage().getActivePart();

						/*
						 * Feign the part has been activated (cause else the
						 * PropertySheet will only take a selection from the
						 * last part it was displaying properties about)
						 */
						sheet.partActivated(activePart);

						// get the current selection
						ISelection sel = activePart.getSite().getSelectionProvider().getSelection();

						// Update the properties view with the current selection
						sheet.selectionChanged(activePart, sel);
					}
				}
			}

			// show the view
			activeWindow.getActivePage().showView(IPageLayout.ID_PROP_SHEET);
		} catch (PartInitException e) {
			log.error("Error opening properties view", e);
		}
	}

}
