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
 * Displays a vertical column of the view-port.
 * 
 * @author Simon Templer
 */
public class VerticalClip implements Clip {

	private float left;
	private float right;

	/**
	 * Create clip displaying a vertical column.
	 * 
	 * @param left the beginning of the column, value between 0 and 1, relative
	 *            to the view-port width
	 * @param right the end of the column, value between 0 and 1, relative to
	 *            the view-port width
	 */
	public VerticalClip(float left, float right) {
		super();
		this.left = (left <= right) ? (left) : (right);
		this.right = (left <= right) ? (right) : (left);
	}

	/**
	 * @see Clip#getClip(Rectangle, int, int, int, int)
	 */
	@Override
	public Shape getClip(Rectangle viewportBounds, int originX, int originY, int width, int height) {
		// column left and right in world pixel coordinates
		int colLeft = (int) (viewportBounds.x + left * viewportBounds.width);
		int colRight = (int) (viewportBounds.x + right * viewportBounds.width);

		// column left and right in local coordinates
		colLeft -= originX;
		colRight -= originX;

		if (colRight < 0) {
			// tile is after column, paint nothing
			return null;
		}

		if (colLeft >= height) {
			// tile is before column, paint nothing
			return null;
		}

		int x1 = Math.max(0, colLeft);
		int x2 = Math.min(height, colRight);

		return new Rectangle(x1, 0, x2 - x1, height);
	}

}
