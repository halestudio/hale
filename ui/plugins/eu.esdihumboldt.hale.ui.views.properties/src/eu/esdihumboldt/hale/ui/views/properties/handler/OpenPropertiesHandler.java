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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.views.properties.PropertySheet;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

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
		try {
			// unpin the property sheet if possible
			IViewReference ref = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					.findViewReference(IPageLayout.ID_PROP_SHEET);
			if (ref != null) {
				IViewPart part = ref.getView(false);
				if (part instanceof PropertySheet) {
					PropertySheet sheet = (PropertySheet) part;
					if (sheet.isPinned()) {
						sheet.setPinned(false);

						IWorkbenchPart activePart = HandlerUtil.getActivePart(event);

						/*
						 * Feign the part has been activated (cause else the
						 * PropertySheet will only take a selection from the
						 * last part it was displaying properties about)
						 */
						sheet.partActivated(activePart);

						// get the current selection
						ISelection sel = HandlerUtil.getActivePart(event).getSite()
								.getSelectionProvider().getSelection();

						// Update the properties view with the current selection
						sheet.selectionChanged(activePart, sel);
					}
				}
			}

			// show the view
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					.showView(IPageLayout.ID_PROP_SHEET);
		} catch (PartInitException e) {
			log.error("Error opening properties view", e);
		}
		return null;
	}

}
