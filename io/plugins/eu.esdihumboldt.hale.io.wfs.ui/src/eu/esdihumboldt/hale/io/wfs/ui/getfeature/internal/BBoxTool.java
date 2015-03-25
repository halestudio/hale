/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.wfs.ui.getfeature.internal;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import de.fhg.igd.mapviewer.MapTool;
import de.fhg.igd.mapviewer.tools.AbstractMapTool;
import de.fhg.igd.mapviewer.tools.renderer.BoxRenderer;

/**
 * Tool for drawing a bounding box.
 * 
 * @author Simon Templer
 */
public class BBoxTool extends AbstractMapTool {

	/**
	 * Creates a {@link BBoxTool}
	 */
	public BBoxTool() {
		BoxRenderer renderer = new BoxRenderer();

		renderer.setBackColor(new Color(0, 0, 255, 100));
		renderer.setBorderColor(new Color(0, 0, 255, 255));

		setRenderer(renderer);
	}

	@Override
	public void click(MouseEvent me, GeoPosition pos) {
		if (me.getClickCount() == 1) {
			if (getPositions().size() < 2) {
				// add pos
				addPosition(pos);
			}
		}
	}

	@Override
	public void popup(MouseEvent me, GeoPosition pos) {
		reset();
	}

	@Override
	public void pressed(MouseEvent me, GeoPosition pos) {
		// ignore
	}

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
