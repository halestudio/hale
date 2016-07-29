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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;
import org.jdesktop.swingx.mapviewer.JXMapViewer;
import org.jdesktop.swingx.painter.AbstractPainter;

/**
 * MapToolPainter
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class MapToolPainter extends AbstractPainter<JXMapViewer>
		implements MapPainter, MouseListener, MouseMotionListener {

	private static final Log log = LogFactory.getLog(MapToolPainter.class);

	private MapTool mapTool;

	private Point2D mousePos = null;

	private final JXMapViewer mapViewer;

	/**
	 * Constructor
	 * 
	 * @param mapViewer the map viewer
	 */
	public MapToolPainter(JXMapViewer mapViewer) {
		mapViewer.addMouseListener(this);
		mapViewer.addMouseMotionListener(this);

		this.mapViewer = mapViewer;

		setAntialiasing(true);
		setCacheable(false);
	}

	/**
	 * @see AbstractPainter#doPaint(Graphics2D, Object, int, int)
	 */
	@Override
	protected void doPaint(Graphics2D g, JXMapViewer mapViewer, int width, int height) {
		if (mapTool == null || mapTool.getRenderer() == null) {
			return;
		}

		// convert positions to pixels inside viewport
		List<Point2D> points = new ArrayList<Point2D>();

		try {
			for (GeoPosition pos : mapTool.getPositions()) {
				points.add(mapViewer.convertGeoPositionToPoint(pos));
			}

			// call renderer
			mapTool.getRenderer().paint(g, points, mousePos, mapTool);
		} catch (IllegalGeoPositionException e) {
			log.error("Error converting tool positions", e); //$NON-NLS-1$
		}
	}

	/**
	 * @return the mapTool
	 */
	public MapTool getMapTool() {
		return mapTool;
	}

	/**
	 * @param mapTool the mapTool to set
	 */
	public void setMapTool(MapTool mapTool) {
		this.mapTool = mapTool;
	}

	/**
	 * @see MouseMotionListener#mouseDragged(MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent me) {
		mouseMove(me);
	}

	/**
	 * @see MouseMotionListener#mouseMoved(MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent me) {
		mouseMove(me);
	}

	private void mouseMove(MouseEvent me) {
		// save mouse position (viewport pixels)
		mousePos = me.getPoint();
		// repaint if necessary
		if (mapTool != null && mapTool.getRenderer() != null
				&& mapTool.getRenderer().repaintOnMouseMove())
			mapViewer.repaint();
	}

	/**
	 * @see MouseListener#mouseClicked(MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent me) {
		if (mapTool != null) {
			mapTool.mouseClicked(me, mapViewer.convertPointToGeoPosition(me.getPoint()));
		}
	}

	/**
	 * @see MouseListener#mouseEntered(MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent me) {
		// ignore
	}

	/**
	 * @see MouseListener#mouseExited(MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent me) {
		// ignore
	}

	/**
	 * @see MouseListener#mousePressed(MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent me) {
		if (mapTool != null) {
			mapTool.mousePressed(me, mapViewer.convertPointToGeoPosition(me.getPoint()));
		}
	}

	/**
	 * @see MouseListener#mouseReleased(MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent me) {
		if (mapTool != null) {
			mapTool.mouseReleased(me, mapViewer.convertPointToGeoPosition(me.getPoint()));

			// reset cursor
			mapViewer.setCursor(mapTool.getCursor());
		}
	}

	/**
	 * @see MapPainter#setMapKit(BasicMapKit)
	 */
	@Override
	public void setMapKit(BasicMapKit mapKit) {
		// ignore
	}

	@Override
	public String getTipText(Point point) {
		return null;
	}

	/**
	 * @see MapPainter#dispose()
	 */
	@Override
	public void dispose() {
		mapViewer.removeMouseListener(this);
		mapViewer.removeMouseMotionListener(this);
	}

}
