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
