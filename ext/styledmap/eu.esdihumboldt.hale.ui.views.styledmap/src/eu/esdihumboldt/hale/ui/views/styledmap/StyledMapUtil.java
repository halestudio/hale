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

package eu.esdihumboldt.hale.ui.views.styledmap;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import de.cs3d.common.metamodel.helperGeometry.BoundingBox;
import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.waypoints.SelectableWaypoint;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.views.styledmap.painter.AbstractInstancePainter;
import eu.esdihumboldt.hale.ui.views.styledmap.painter.InstanceWaypoint;

/**
 * Utility methods regarding the map view.
 * @author Simon Templer
 */
public abstract class StyledMapUtil {

	/**
	 * Zoom to all instances available in the map. Does nothing if there are
	 * no instances displayed.
	 * @param mapKit the map kit
	 */
	public static void zoomToAll(BasicMapKit mapKit) {
		BoundingBox bb = null;
		
		// determine bounding box
		for (AbstractInstancePainter painter : mapKit.getTilePainters(AbstractInstancePainter.class)) {
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
			positions.add(new GeoPosition(bb.getMinX(), bb.getMinY(), 
					SelectableWaypoint.COMMON_EPSG));
			positions.add(new GeoPosition(bb.getMaxX(), bb.getMaxY(), 
					SelectableWaypoint.COMMON_EPSG));
			mapKit.zoomToPositions(positions);
		}
	}
	
	/**
	 * Zoom to the selected instances. Does nothing if the selection is empty
	 * or contains no {@link Instance}s or {@link InstanceReference}s.
	 * @param mapKit the map kit
	 * @param selection the selection
	 */
	public static void zoomToSelection(BasicMapKit mapKit, 
			IStructuredSelection selection) {
		BoundingBox bb = null;
		
		// determine bounding box for each reference and accumulate it
		for (AbstractInstancePainter painter : mapKit.getTilePainters(AbstractInstancePainter.class)) {
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
			positions.add(new GeoPosition(bb.getMinX(), bb.getMinY(), 
					SelectableWaypoint.COMMON_EPSG));
			positions.add(new GeoPosition(bb.getMaxX(), bb.getMaxY(), 
					SelectableWaypoint.COMMON_EPSG));
			mapKit.zoomToPositions(positions);
		}
	}

	private static InstanceReference getReference(Object element) {
		if (element instanceof InstanceReference) {
			return (InstanceReference) element;
		}
		if (element instanceof Instance) {
			InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
			is.getReference((Instance) element);
		}
		return null;
	}
	
}
