/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.util.svg.test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Settings for SVG painting.
 * 
 * @author Simon Templer
 */
public class PaintSettings {

	private final double scaleFactor;

	private final double minX;

	private final double minY;

	private final int pointSize;

	/**
	 * Create a new settings object based on the given values.
	 * 
	 * @param scaleFactor the scale factor
	 * @param minX the offset on the X axis (which is subtracted from X
	 *            ordinates)
	 * @param minY the offset on the Y axis (which is subtracted from Y
	 *            ordinates)
	 * @param pointSize the size of individual drawn points
	 */
	public PaintSettings(double scaleFactor, double minX, double minY, int pointSize) {
		super();
		this.scaleFactor = scaleFactor;
		this.minX = minX;
		this.minY = minY;
		this.pointSize = pointSize;
	}

	/**
	 * Create a new settings object based on an evelope.
	 * 
	 * @param envelope the envelope
	 * @param maxSize the maximum size of the draw area
	 * @param pointSize the size of individual drawn points
	 */
	public PaintSettings(Envelope envelope, int maxSize, int pointSize) {
		int height;
		int width;
		if (envelope.getHeight() > envelope.getWidth()) {
			height = maxSize;
			width = (int) Math.ceil(height * envelope.getWidth() / envelope.getHeight());
			scaleFactor = height / envelope.getHeight();
		}
		else {
			width = maxSize;
			height = (int) Math.ceil(width * envelope.getHeight() / envelope.getWidth());
			scaleFactor = width / envelope.getWidth();
		}
		minX = envelope.getMinX();
		minY = envelope.getMinY();

		this.pointSize = pointSize;
	}

	/**
	 * @return the scaleFactor
	 */
	public double getScaleFactor() {
		return scaleFactor;
	}

	/**
	 * @return the minX
	 */
	public double getMinX() {
		return minX;
	}

	/**
	 * @return the minY
	 */
	public double getMinY() {
		return minY;
	}

	/**
	 * @return the pointSize
	 */
	public int getPointSize() {
		return pointSize;
	}

	/**
	 * Convert a coordinate according to the paint settings (scaling etc.).
	 * 
	 * @param coord the coordinate to convert
	 * @return the converted coordinate
	 */
	public Coordinate convertPoint(Coordinate coord) {
		return new Coordinate((int) Math.round((coord.x - getMinX()) * getScaleFactor()),
				(int) Math.round((coord.y - getMinY()) * getScaleFactor()));
	}

}
