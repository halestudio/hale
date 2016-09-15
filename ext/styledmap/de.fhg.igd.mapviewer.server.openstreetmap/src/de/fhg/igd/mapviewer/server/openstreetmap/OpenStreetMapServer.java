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
package de.fhg.igd.mapviewer.server.openstreetmap;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jdesktop.swingx.mapviewer.GeotoolsConverter;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.TileFactoryInfoTileProvider;

import de.fhg.igd.mapviewer.MapPainter;
import de.fhg.igd.mapviewer.painter.TextPainter;
import de.fhg.igd.mapviewer.server.AbstractMapServer;
import de.fhg.igd.mapviewer.server.CustomTileFactory;
import de.fhg.igd.mapviewer.server.MapServer;

/**
 * OpenStreetMapServer
 *
 * @author Simon Templer
 * @deprecated The Mapquest OSM tiles are no longer available, use the
 *             configurable custom tile maps instead.
 */
@Deprecated
public class OpenStreetMapServer extends AbstractMapServer {

	private CustomTileFactory fact;

	/**
	 * @see MapServer#getTileFactory(TileCache)
	 */
	@Override
	public TileFactory getTileFactory(TileCache cache) {
		final int max = 18;
		TileFactoryInfo info = new TileFactoryInfo(0, max - 2, max, 256, true, true, // tile
																						// size
																						// is
																						// 256
																						// and
																						// x/y
																						// orientation
																						// is
																						// normal
				"x", "y", "z", "http://otile1.mqcdn.com/tiles/1.0.0/osm", // 5/15/10.png"
				"http://otile2.mqcdn.com/tiles/1.0.0/osm",
				"http://otile3.mqcdn.com/tiles/1.0.0/osm",
				"http://otile4.mqcdn.com/tiles/1.0.0/osm") {

			@Override
			public String[] getTileUrls(int x, int y, int zoom) {
				zoom = max - zoom;
				String[] result = new String[baseURLs.length];
				for (int i = 0; i < baseURLs.length; ++i) {
					String url = this.baseURLs[i] + "/" + zoom + "/" + x + "/" + y + ".png";
					result[i] = url;
				}
				return result;
			}

		};
		info.setDefaultZoomLevel(15);

		fact = new CustomTileFactory(
				new TileFactoryInfoTileProvider(info, GeotoolsConverter.getInstance()), cache);
		return fact;
	}

	/**
	 * @see MapServer#cleanup()
	 */
	@Override
	public void cleanup() {
		if (fact != null) {
			fact.cleanup();
		}
	}

	@Override
	public MapPainter getMapOverlay() {
		return new TextPainter(true) {

			@Override
			protected void configureGraphics(Graphics2D g) {
				setAntialiasing(false);

				super.configureGraphics(g);

				g.setPaint(Color.BLACK);
			}

			@Override
			protected Color getBorderColor() {
				return Color.WHITE;
			}

			@Override
			protected String getText() {
				return "Data, imagery and map information provided by MapQuest, Open Street Map and contributors, CC-BY-SA";
			}
		};
	}

}
