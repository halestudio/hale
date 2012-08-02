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

package eu.esdihumboldt.hale.ui.views.styledmap.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.fhg.igd.mapviewer.view.MapView;
import eu.esdihumboldt.hale.ui.views.styledmap.StyledMapUtil;
import eu.esdihumboldt.hale.ui.views.styledmap.StyledMapView;

/**
 * Zooms to selected instances.
 * @author Simon Templer
 */
public class ZoomToSelectionHandler extends AbstractHandler {

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IViewPart viewPart = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(StyledMapView.ID);
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if (viewPart instanceof MapView 
				&& selection instanceof IStructuredSelection) {
			StyledMapUtil.zoomToSelection(
					((MapView) viewPart).getMapKit(),
					(IStructuredSelection) selection);
		}
		
		return null;
	}

}
