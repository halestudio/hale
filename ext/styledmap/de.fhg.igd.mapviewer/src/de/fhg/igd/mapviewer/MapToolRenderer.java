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
package de.fhg.igd.mapviewer;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * MapToolRenderer
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public interface MapToolRenderer {

	/**
	 * Paints the tool
	 * 
	 * @param g the graphics device
	 * @param points the list of collected points
	 * @param mousePos the current mouse position
	 * @param tool the map tool
	 */
	public void paint(final Graphics2D g, final List<Point2D> points, final Point2D mousePos,
			final MapTool tool);

	/**
	 * Determines if the tool has to be repainted on mouse move
	 * 
	 * @return if the tool has to be repainted on mouse move
	 */
	public boolean repaintOnMouseMove();

}
