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
package de.fhg.igd.mapviewer.tools.pointer;

import java.awt.event.MouseEvent;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import de.fhg.igd.mapviewer.MapTool;
import de.fhg.igd.mapviewer.tools.AbstractMapTool;

/**
 * Tool that does nothing
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class PointerTool extends AbstractMapTool {

	/**
	 * @see AbstractMapTool#click(MouseEvent, GeoPosition)
	 */
	@Override
	public void click(MouseEvent me, GeoPosition pos) {
		if (me.getClickCount() == 2) {
			mapKit.setCenterPosition(pos);
			mapKit.setZoom(mapKit.getMainMap().getZoom() - 1);
		}
	}

	/**
	 * @see AbstractMapTool#popup(MouseEvent, GeoPosition)
	 */
	@Override
	public void popup(MouseEvent me, GeoPosition pos) {
		// ignore
	}

	/**
	 * @see AbstractMapTool#released(MouseEvent, GeoPosition)
	 */
	@Override
	public void released(MouseEvent me, GeoPosition pos) {
		// ignore
	}

	/**
	 * @see AbstractMapTool#pressed(MouseEvent, GeoPosition)
	 */
	@Override
	public void pressed(MouseEvent me, GeoPosition pos) {
		// ignore
	}

	/**
	 * @see MapTool#isPanEnabled()
	 */
	@Override
	public boolean isPanEnabled() {
		return true;
	}

}
