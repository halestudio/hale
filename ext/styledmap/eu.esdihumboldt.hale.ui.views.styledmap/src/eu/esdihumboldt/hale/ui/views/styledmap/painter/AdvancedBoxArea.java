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

import de.fhg.igd.mapviewer.marker.area.Area;
import de.fhg.igd.mapviewer.marker.area.BoxArea;

/**
 * Create a box area where an AWT area provides a more accurate
 * {@link #contains(int, int)}.
 * 
 * @author Sebastian Reinhardt
 */
public class AdvancedBoxArea extends BoxArea {

	private final java.awt.geom.Area area;

	/**
	 * @param minX the minimum x value of the area
	 * @param minY the minimum y value of the area
	 * @param maxX the maximum x value of the area
	 * @param maxY the maximum y value of the area
	 */
	public AdvancedBoxArea(java.awt.geom.Area area, int minX, int minY, int maxX, int maxY) {
		super(minX, minY, maxX, maxY);

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
