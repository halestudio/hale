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

package de.fhg.igd.mapviewer;

import java.awt.image.BufferedImageOp;

import org.jdesktop.swingx.mapviewer.GeoPosition;

/**
 * Refreshes tiles that lie within added areas
 * 
 * @author Simon Templer
 * @author Michel Kraemer
 */
public interface Refresher {

	/**
	 * Set an image operation that is applied to an invalidated image that is
	 * only present until replaced by the newly drawn tile.
	 * 
	 * @param imageOp the imageOp to set
	 */
	void setImageOp(BufferedImageOp imageOp);

	/**
	 * Add a position
	 * 
	 * @param pos the position
	 */
	void addPosition(GeoPosition pos);

	/**
	 * Adds an area (a bounding box)
	 * 
	 * @param topLeft the top left corner of the bounding box
	 * @param bottomRight the bottom right corner of the bounding box
	 */
	void addArea(GeoPosition topLeft, GeoPosition bottomRight);

	/**
	 * Execute the refresh
	 */
	void execute();
}
