/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.marker;

import java.awt.Color;

/**
 * A simple circle marker with given colors
 * 
 * @author Simon Templer
 */
public class SimpleCircleMarker extends CircleMarker<Object> {

	private final Color borderColor;

	private final Color paintColor;

	private final Color markerColor;

	private final boolean showMarker;

	/**
	 * Constructor
	 * 
	 * @param size the circle size
	 * @param paintColor the fill color of the circle
	 * @param borderColor the border color of the circle
	 * @param markerColor the selection marker color
	 * @param showMarker if the selection marker shall be shown
	 */
	public SimpleCircleMarker(int size, Color paintColor, Color borderColor, Color markerColor,
			boolean showMarker) {
		super(size);

		this.borderColor = borderColor;
		this.paintColor = paintColor;
		this.markerColor = markerColor;
		this.showMarker = showMarker;
	}

	/**
	 * @see CircleMarker#getBorderColor(Object)
	 */
	@Override
	protected Color getBorderColor(Object context) {
		return borderColor;
	}

	/**
	 * @see CircleMarker#getPaintColor(Object)
	 */
	@Override
	protected Color getPaintColor(Object context) {
		return paintColor;
	}

	/**
	 * @see CircleMarker#getMarkerColor(Object)
	 */
	@Override
	protected Color getMarkerColor(Object context) {
		return markerColor;
	}

	/**
	 * @see CircleMarker#showMarker(Object)
	 */
	@Override
	protected boolean showMarker(Object context) {
		return showMarker;
	}

}
