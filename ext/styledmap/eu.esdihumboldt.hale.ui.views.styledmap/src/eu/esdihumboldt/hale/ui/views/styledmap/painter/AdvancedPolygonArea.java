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
