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

package eu.esdihumboldt.hale.ui.views.styledmap;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.geom.BoundingBox;
import de.fhg.igd.mapviewer.waypoints.SelectableWaypoint;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.views.styledmap.painter.AbstractInstancePainter;
import eu.esdihumboldt.hale.ui.views.styledmap.painter.InstanceWaypoint;

/**
 * Utility methods regarding the map view.
 * 
 * @author Simon Templer
 */
public abstract class StyledMapUtil {

	/**
	 * Zoom to all instances available in the map. Does nothing if there are no
	 * instances displayed.
	 * 
	 * @param mapKit the map kit
	 */
	public static void zoomToAll(BasicMapKit mapKit) {
		BoundingBox bb = null;

		// determine bounding box
		for (AbstractInstancePainter painter : mapKit
				.getTilePainters(AbstractInstancePainter.class)) {
			BoundingBox painterBB = painter.getBoundingBox();
			if (painterBB.checkIntegrity() && !painterBB.isEmpty()) {
				if (bb == null) {
					bb = new BoundingBox(painterBB);
				}
				else {
					bb.add(painterBB);
				}
			}
		}

		if (bb != null) {
			Set<GeoPosition> positions = new HashSet<GeoPosition>();
			positions.add(
					new GeoPosition(bb.getMinX(), bb.getMinY(), SelectableWaypoint.COMMON_EPSG));
			positions.add(
					new GeoPosition(bb.getMaxX(), bb.getMaxY(), SelectableWaypoint.COMMON_EPSG));
			mapKit.zoomToPositions(positions);
		}
	}

	/**
	 * Zoom to the selected instances. Does nothing if the selection is empty or
	 * contains no {@link Instance}s or {@link InstanceReference}s.
	 * 
	 * @param mapKit the map kit
	 * @param selection the selection
	 */
	public static void zoomToSelection(BasicMapKit mapKit, IStructuredSelection selection) {
		BoundingBox bb = null;

		// determine bounding box for each reference and accumulate it
		for (AbstractInstancePainter painter : mapKit
				.getTilePainters(AbstractInstancePainter.class)) {
			for (Object element : selection.toList()) {
				InstanceReference ref = getReference(element);
				if (ref != null) {
					InstanceWaypoint wp = painter.findWaypoint(ref);
					if (wp != null) {
						BoundingBox wpBB = wp.getBoundingBox();
						if (wpBB.checkIntegrity() && !wpBB.isEmpty()) {
							if (bb == null) {
								bb = new BoundingBox(wpBB);
							}
							else {
								bb.add(wpBB);
							}
						}
					}
				}
			}
		}

		if (bb != null) {
			Set<GeoPosition> positions = new HashSet<GeoPosition>();
			positions.add(
					new GeoPosition(bb.getMinX(), bb.getMinY(), SelectableWaypoint.COMMON_EPSG));
			positions.add(
					new GeoPosition(bb.getMaxX(), bb.getMaxY(), SelectableWaypoint.COMMON_EPSG));
			mapKit.zoomToPositions(positions);
		}
	}

	private static InstanceReference getReference(Object element) {
		if (element instanceof InstanceReference) {
			return (InstanceReference) element;
		}
		if (element instanceof Instance) {
			InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);
			is.getReference((Instance) element);
		}
		return null;
	}

}
