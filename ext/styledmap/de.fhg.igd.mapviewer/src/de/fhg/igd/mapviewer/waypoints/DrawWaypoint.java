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
package de.fhg.igd.mapviewer.waypoints;

import java.awt.Graphics2D;

import org.jdesktop.swingx.mapviewer.Waypoint;

/**
 * DrawWaypoint
 *
 * @param <W> the way-point type
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 * @version $Id$
 */
public class DrawWaypoint<W extends Waypoint> {

	private final W wp;

	private final Graphics2D g;

	/**
	 * Constructor
	 * 
	 * @param wp the way-point
	 * @param g the graphics
	 */
	public DrawWaypoint(W wp, Graphics2D g) {
		this.wp = wp;
		this.g = g;
	}

	/**
	 * @return the wp
	 */
	public W getWaypoint() {
		return wp;
	}

	/**
	 * @return the g
	 */
	public Graphics2D getGraphics() {
		return g;
	}

}
