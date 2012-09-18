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

package eu.esdihumboldt.hale.ui.views.styledmap.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.fhg.igd.mapviewer.view.MapView;
import eu.esdihumboldt.hale.ui.views.styledmap.StyledMapUtil;
import eu.esdihumboldt.hale.ui.views.styledmap.StyledMapView;

/**
 * Zooms to all instances present in the map.
 * 
 * @author Simon Templer
 */
public class ZoomToAllHandler extends AbstractHandler {

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IViewPart viewPart = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
				.findView(StyledMapView.ID);

		if (viewPart instanceof MapView) {
			StyledMapUtil.zoomToAll(((MapView) viewPart).getMapKit());
		}

		return null;
	}

}
