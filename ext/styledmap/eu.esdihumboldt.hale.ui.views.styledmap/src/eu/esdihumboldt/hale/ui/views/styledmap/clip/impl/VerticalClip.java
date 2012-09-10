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
