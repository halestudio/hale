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

import java.awt.Rectangle;
import java.awt.Shape;

import eu.esdihumboldt.hale.ui.views.styledmap.clip.Clip;

/**
 * Displays horizontal rows of the view-port.
 * 
 * @author Simon Templer
 */
public class HorizontalClip implements Clip {

	private float top;
	private float bottom;

	/**
	 * Create clip displaying a horizontal row.
	 * 
	 * @param top the top of the row, value between 0 and 1, relative to the
	 *            view-port height
	 * @param bottom the bottom of the row, value between 0 and 1, relative to
	 *            the view-port height
	 */
	public HorizontalClip(float top, float bottom) {
		super();
		this.top = (top <= bottom) ? (top) : (bottom);
		this.bottom = (top <= bottom) ? (bottom) : (top);
	}

	/**
	 * @see Clip#getClip(Rectangle, int, int, int, int)
	 */
	@Override
	public Shape getClip(Rectangle viewportBounds, int originX, int originY, int width, int height) {
		// row top and bottom in world pixel coordinates
		int rowTop = (int) (viewportBounds.y + top * viewportBounds.height);
		int rowBottom = (int) (viewportBounds.y + bottom * viewportBounds.height);

		// row top and bottom in local coordinates
		rowTop -= originY;
		rowBottom -= originY;

		if (rowBottom < 0) {
			// tile is below row, paint nothing
			return null;
		}

		if (rowTop >= height) {
			// tile is above row, paint nothing
			return null;
		}

		int y1 = Math.max(0, rowTop);
		int y2 = Math.min(height, rowBottom);

		return new Rectangle(0, y1, width, y2 - y1);
	}

}
