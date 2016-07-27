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

package de.fhg.igd.mapviewer.tip;

import org.jdesktop.swingx.mapviewer.JXMapViewer;

import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.MapPainter;

/**
 * Map tip interface
 * 
 * @author Simon Templer
 */
public interface MapTip {

	/**
	 * Get the last tip text
	 * 
	 * @return the last tip text
	 */
	public abstract String getLastText();

	/**
	 * Get the tip painter for use in a {@link MapTipManager} or
	 * {@link BasicMapKit}
	 * 
	 * @return the tip painter
	 */
	public abstract MapPainter getPainter();

	/**
	 * Determines if the tip wants to paint something
	 * 
	 * @return if the tip wants to paint
	 */
	public abstract boolean wantsToPaint();

	/**
	 * Initialize the map tip with the given map
	 * 
	 * @param map the map
	 */
	void init(JXMapViewer map);

}
