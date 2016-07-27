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
package de.fhg.igd.mapviewer.tools.boxzoom;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.HashSet;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import de.fhg.igd.mapviewer.MapTool;
import de.fhg.igd.mapviewer.tools.AbstractMapTool;
import de.fhg.igd.mapviewer.tools.renderer.BoxRenderer;
import de.fhg.igd.mapviewer.view.arecalculation.AreaCalc;

/**
 * BoxZoomTool
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class BoxZoomTool extends AbstractMapTool {

	/**
	 * Creates a {@link BoxZoomTool}
	 */
	public BoxZoomTool() {
		BoxRenderer renderer = new BoxRenderer();

		renderer.setBackColor(new Color(79, 79, 79, 100));
		renderer.setBorderColor(new Color(79, 79, 79, 255));

		setRenderer(renderer);
	}

	/**
	 * @see AbstractMapTool#click(MouseEvent, GeoPosition)
	 */
	@Override
	public void click(MouseEvent me, GeoPosition pos) {
		// set selection type FIXME there should another mechanism for this, not
		// needing a dependency to AreaCalc
		AreaCalc.getInstance().setSelectionType("rectangle");

		if (me.getClickCount() == 1) {
			if (getPositions().size() < 1) {
				// add pos
				addPosition(pos);
			}
			else {
				// action & reset
				addPosition(pos);
				mapKit.zoomToPositions(new HashSet<GeoPosition>(getPositions()));
				reset();
			}
		}
	}

	/**
	 * @see AbstractMapTool#popup(MouseEvent, GeoPosition)
	 */
	@Override
	public void popup(MouseEvent me, GeoPosition pos) {
		reset();
	}

	/**
	 * @see AbstractMapTool#pressed(MouseEvent, GeoPosition)
	 */
	@Override
	public void pressed(MouseEvent me, GeoPosition pos) {
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
	 * @see AbstractMapTool#getCursor()
	 */
	@Override
	public Cursor getCursor() {
		return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	}

	/**
	 * @see MapTool#isPanEnabled()
	 */
	@Override
	public boolean isPanEnabled() {
		return true;
	}

}
