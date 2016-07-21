/*
 * WaypointRenderer.java
 *
 * Created on March 30, 2006, 5:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

import java.awt.Graphics2D;

/**
 * A interface that draws waypoints. Implementations of WaypointRenderer can be
 * set on a WayPointPainter to draw waypoints on a JXMapViewer
 * 
 * @author joshua.marinacci@sun.com
 */
public interface WaypointRenderer {

	/**
	 * Paint the specified way-point on the specified map and graphics context
	 * 
	 * @param g the graphics device
	 * @param map the map viewer
	 * @param waypoint the way-point
	 * @return false
	 */
	public boolean paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint waypoint);

}
