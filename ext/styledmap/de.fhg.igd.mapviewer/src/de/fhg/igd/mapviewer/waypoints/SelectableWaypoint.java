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
package de.fhg.igd.mapviewer.waypoints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.GeotoolsConverter;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;
import org.jdesktop.swingx.mapviewer.Waypoint;

import de.fhg.igd.mapviewer.Refresher;
import de.fhg.igd.mapviewer.geom.BoundingBox;
import de.fhg.igd.mapviewer.geom.Localizable;
import de.fhg.igd.mapviewer.marker.Marker;

/**
 * SelectableWaypoint
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 * @param <W> the way-point type
 */
public class SelectableWaypoint<W extends SelectableWaypoint<W>> extends Waypoint
		implements Localizable {

	private static final Log log = LogFactory.getLog(SelectableWaypoint.class);

	/**
	 * The EPSG code of the SRS used for the bounding box
	 */
	public static final int COMMON_EPSG = 3395; // WGS 84 / World Mercator
	// 4326; // WGS 84
	// 4087; // WGS 84 / World Equidistant Cylindrical

	private static final double POSITION_EXPAND = 0.125;
	// 1e-8;

	private boolean selected = false;

	private BoundingBox box = null;

	/**
	 * States if the way-point only represents a point (instead of a larger
	 * geometry)
	 */
	private boolean point;

	private Marker<? super W> marker;

	/**
	 * Constructor
	 * 
	 * @param x the x ordinate
	 * @param y the y ordinate
	 * @param epsg the EPSG code
	 * @param bb the bounding box, may be <code>null</code>
	 */
	public SelectableWaypoint(double x, double y, int epsg, BoundingBox bb) {
		super(x, y, epsg);

		if (bb != null && bb.checkIntegrity()) {
			this.box = bb;
			this.point = false;
		}
		else {
			this.point = true;
		}
	}

	/**
	 * Constructor
	 * 
	 * @param coord the position of the way-point
	 * @param bb the bounding box, may be <code>null</code>
	 */
	public SelectableWaypoint(GeoPosition coord, BoundingBox bb) {
		super(coord);

		if (bb != null && bb.checkIntegrity()) {
			this.box = bb;
			this.point = false;
		}
		else {
			this.point = true;
		}
	}

	/**
	 * @return the point
	 */
	public boolean isPoint() {
		return point;
	}

	/**
	 * @see Waypoint#setPosition(GeoPosition)
	 */
	@Override
	public void setPosition(GeoPosition coordinate) {
		super.setPosition(coordinate);

		box = null;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Set if the way-point is selected
	 * 
	 * @param selected if the way-point is selected
	 * @param refresh the refresher
	 */
	public void setSelected(boolean selected, Refresher refresh) {
		this.selected = selected;

		if (refresh != null) {
			addToRefresher(refresh);
		}
	}

	/**
	 * Add a way-point to the refresher
	 * 
	 * @param refresher the refresher
	 */
	public void addToRefresher(Refresher refresher) {
		if (isPoint()) {
			refresher.addPosition(getPosition());
		}
		else {
			BoundingBox bb = getBoundingBox();
			GeoPosition bottomRight = new GeoPosition(bb.getMaxX(), bb.getMaxY(), COMMON_EPSG);
			GeoPosition topLeft = new GeoPosition(bb.getMinX(), bb.getMinY(), COMMON_EPSG);
			refresher.addArea(topLeft, bottomRight);
		}
	}

	/**
	 * @return the marker
	 */
	public Marker<? super W> getMarker() {
		return marker;
	}

	/**
	 * @param marker the marker to set
	 */
	public void setMarker(Marker<? super W> marker) {
		this.marker = marker;
	}

	/**
	 * @see Localizable#getBoundingBox()
	 */
	@Override
	public BoundingBox getBoundingBox() {
		if (box == null) {
			GeoPosition pos;
			try {
				pos = GeotoolsConverter.getInstance().convert(getPosition(), COMMON_EPSG);
				box = new BoundingBox(pos.getX() - POSITION_EXPAND, pos.getY() - POSITION_EXPAND,
						0.0, pos.getX() + POSITION_EXPAND, pos.getY() + POSITION_EXPAND, 1.0);
			} catch (IllegalGeoPositionException e) {
				log.warn("Error creating bounding box for waypoint"); //$NON-NLS-1$
			}
		}

		return box;
	}

}
