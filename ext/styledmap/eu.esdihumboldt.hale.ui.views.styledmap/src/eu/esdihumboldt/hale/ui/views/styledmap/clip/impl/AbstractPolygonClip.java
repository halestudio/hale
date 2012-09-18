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

package eu.esdihumboldt.hale.ui.views.styledmap.clip.impl;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;

import eu.esdihumboldt.hale.ui.views.styledmap.clip.Clip;

/**
 * Base class for clip's based on a polygon defined on the view-port.
 * 
 * @author Simon Templer
 */
public abstract class AbstractPolygonClip implements Clip {

	/**
	 * @see Clip#getClip(Rectangle, int, int, int, int)
	 */
	@Override
	public Shape getClip(Rectangle viewportBounds, int originX, int originY, int width, int height) {
		// visible area in world pixel coordinates
		Polygon visible = getVisiblePolygon(viewportBounds);
		// visible area in local pixel coordinates
		visible.translate(-originX, -originY);
		// tile area
		Rectangle tileRect = new Rectangle(0, 0, width, height);

		if (visible.contains(tileRect)) {
			// contained whole
			return tileRect;
		}
		else if (!visible.intersects(tileRect)) {
			// don't paint
			return null;
		}
		else {
			// intersection
			Area visibleArea = new Area(visible);
			Area tileArea = new Area(tileRect);

			visibleArea.intersect(tileArea);
			return visibleArea;
		}
	}

	/**
	 * Get the visible area.
	 * 
	 * @param viewportBounds the view-port bounds
	 * @return the visible area in world pixel coordinates
	 */
	protected abstract Polygon getVisiblePolygon(Rectangle viewportBounds);

}
