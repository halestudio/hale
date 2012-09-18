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
	 * Create an area.
	 * 
	 * @param area AWT area to use for the {@link #contains(int, int)} check
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
