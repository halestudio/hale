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

import java.awt.Dimension;

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

	private final double maxY;

	private final int pointSize;

	private final Dimension canvasSize;

	/**
	 * Create a new settings object based on the given values.
	 * 
	 * @param scaleFactor the scale factor
	 * @param minX the offset on the X axis (which is subtracted from X
	 *            ordinates)
	 * @param maxY the offset on the Y axis (which Y ordinates are substracted
	 *            from)
	 * @param pointSize the size of individual drawn points
	 * @param canvasSize the canvas size or <code>null</code>
	 */
	public PaintSettings(double scaleFactor, double minX, double maxY, int pointSize,
			Dimension canvasSize) {
		super();
		this.scaleFactor = scaleFactor;
		this.minX = minX;
		this.maxY = maxY;
		this.pointSize = pointSize;
		this.canvasSize = canvasSize;
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
		maxY = envelope.getMaxY();

		canvasSize = new Dimension(width, height);

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
	public double getMaxY() {
		return maxY;
	}

	/**
	 * @return the pointSize
	 */
	public int getPointSize() {
		return pointSize;
	}

	/**
	 * @return the canvasSize
	 */
	public Dimension getCanvasSize() {
		return canvasSize;
	}

	/**
	 * Convert an x ordinate according to the paint settings (scaling etc.).
	 * 
	 * @param x the x ordinate
	 * @return the converted x ordinate
	 */
	public int convertX(double x) {
		return (int) Math.round((x - getMinX()) * getScaleFactor());
	}

	/**
	 * Convert an y ordinate according to the paint settings (scaling etc.).
	 * 
	 * @param y the y ordinate
	 * @return the converted y ordinate
	 */
	public int convertY(double y) {
		return (int) Math.round((getMaxY() - y) * getScaleFactor());
	}

	/**
	 * Convert a coordinate according to the paint settings (scaling etc.).
	 * 
	 * @param coord the coordinate to convert
	 * @return the converted coordinate
	 */
	public Coordinate convertPoint(Coordinate coord) {
		return new Coordinate(convertX(coord.x), convertY(coord.y));
	}

}
