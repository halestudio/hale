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
