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

package eu.esdihumboldt.hale.ui.views.styledmap.tool;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;

import de.fhg.igd.mapviewer.MapTool;
import de.fhg.igd.mapviewer.tools.AbstractMapTool;
import de.fhg.igd.mapviewer.tools.renderer.BoxRenderer;

/**
 * Tool for selecting instances, either by a click or through a selection box.
 * 
 * @author Simon Templer
 */
public class InstanceTool extends AbstractInstanceTool {

	private static final Log log = LogFactory.getLog(InstanceTool.class);

	/**
	 * Default constructor
	 */
	public InstanceTool() {
		BoxRenderer renderer = new BoxRenderer();

		renderer.setBackColor(new Color(0, 255, 255, 50));
		renderer.setBorderColor(new Color(0, 255, 255, 255));

		setRenderer(renderer);
	}

	/**
	 * @see AbstractMapTool#click(MouseEvent, GeoPosition)
	 */
	@Override
	public void click(MouseEvent me, GeoPosition pos) {
		if (me.getClickCount() == 2) {
			mapKit.setCenterPosition(pos);
			mapKit.setZoom(mapKit.getMainMap().getZoom() - 1);
		}
		else if (me.getClickCount() == 1) {
			if (me.isAltDown() && getPositions().size() < 1) {
				// add pos
				addPosition(pos);
			}
			else if (getPositions().size() == 1) {
				// finish box selection
				// action & reset
				addPosition(pos);

				// action
				try {
					List<Point2D> points = getPoints();
					Rectangle rect = new Rectangle((int) points.get(0).getX(), (int) points.get(0)
							.getY(), 0, 0);
					rect.add(points.get(1));

					updateSelection(rect, me.isControlDown() || me.isMetaDown(), true);
				} catch (IllegalGeoPositionException e) {
					log.error("Error calculating selection box", e); //$NON-NLS-1$
				}

				reset();
			}
			else {
				// click selection
				reset();

				updateSelection(me.getPoint(), me.isControlDown() || me.isMetaDown(), true);
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
	 * @see MapTool#isPanEnabled()
	 */
	@Override
	public boolean isPanEnabled() {
		return true;
	}

}
