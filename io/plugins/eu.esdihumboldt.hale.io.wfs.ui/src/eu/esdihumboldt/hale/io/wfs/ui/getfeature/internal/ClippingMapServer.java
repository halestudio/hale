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

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileFactory;

import de.fhg.igd.mapviewer.MapPainter;
import de.fhg.igd.mapviewer.server.ClippingTileProviderDecorator;
import de.fhg.igd.mapviewer.server.CustomTileFactory;
import de.fhg.igd.mapviewer.server.MapServer;

/**
 * Map server that clips the map to a specific region.
 * 
 * @author Simon Templer
 */
public class ClippingMapServer implements MapServer {

	private final MapServer server;
	private final GeoPosition topLeft;
	private final GeoPosition bottomRight;

	/**
	 * Decorates the given map server and applies clipping.
	 * 
	 * @param server the map server
	 * @param topLeft the top left corner of the region that is clipped to
	 * @param bottomRight the bottom right corner of the region that is clipped
	 *            to
	 */
	public ClippingMapServer(MapServer server, GeoPosition topLeft, GeoPosition bottomRight) {
		super();
		this.server = server;
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}

	@Override
	public void setName(String paramString) {
		server.setName(paramString);
	}

	@Override
	public String getName() {
		return server.getName();
	}

	@Override
	public TileFactory getTileFactory(TileCache cache) {
		TileFactory org = server.getTileFactory(cache);
		ClippingTileProviderDecorator clipper = new ClippingTileProviderDecorator(
				org.getTileProvider(), topLeft, bottomRight);
		return new CustomTileFactory(clipper, cache);
	}

	@Override
	public MapPainter getMapOverlay() {
		return server.getMapOverlay();
	}

	@Override
	public void cleanup() {
		server.cleanup();
	}

}
