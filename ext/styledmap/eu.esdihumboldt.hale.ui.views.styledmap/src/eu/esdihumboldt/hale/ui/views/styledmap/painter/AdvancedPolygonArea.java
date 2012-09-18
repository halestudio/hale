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

package eu.esdihumboldt.hale.ui.views.styledmap.painter;

import java.awt.Polygon;

import de.fhg.igd.mapviewer.marker.area.Area;
import de.fhg.igd.mapviewer.marker.area.PolygonArea;

/**
 * Create a polygon area where an AWT area provides a more accurate
 * {@link #contains(int, int)}, e.g. for polygons with holes.
 * 
 * @author Simon Templer
 */
public class AdvancedPolygonArea extends PolygonArea {

	private final java.awt.geom.Area area;

	/**
	 * Create a marker area.
	 * 
	 * @param area the polygon area
	 * @param exterior the exterior polygon
	 */
	public AdvancedPolygonArea(java.awt.geom.Area area, Polygon exterior) {
		super(exterior);
		this.area = area;
	}

	/**
	 * @see Area#contains(int, int)
	 */
	@Override
	public boolean contains(int x, int y) {
		return area.contains(x, y);
	}

}
