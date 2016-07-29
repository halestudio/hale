/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package de.fhg.igd.mapviewer.server.tiles;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jdesktop.swingx.mapviewer.GeotoolsConverter;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.TileFactoryInfoTileProvider;

import de.fhg.igd.mapviewer.MapPainter;
import de.fhg.igd.mapviewer.painter.TextPainter;
import de.fhg.igd.mapviewer.server.CustomTileFactory;
import de.fhg.igd.mapviewer.server.MapServer;

/**
 * CustomTileMapServer. gets all the tiles from the URL-Pattern provided.
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
@SuppressWarnings("deprecation")
public class CustomTileMapServer extends CustomTileMapServerConfiguration {

	private CustomTileFactory fact;

	/**
	 * Default constructor
	 * 
	 */
	public CustomTileMapServer() {

	}

	/**
	 * @see MapServer#getTileFactory(TileCache)
	 */
	@Override
	public TileFactory getTileFactory(TileCache cache) {

		final int max = CustomTileMapServer.this.getZoomLevel() - 1;

		final int defaultMax = (max >= 2) ? (max - 2) : (max);

		TileFactoryInfo info = new TileFactoryInfo(0, defaultMax, max, 256, true, true, "x", "y",
				"z") {

			@Override
			public String[] getTileUrls(int x, int y, int zoom) {
				zoom = max - zoom;
				// http://tile.stamen.com/watercolor/{z}/{x}/{y}.jpg

				String createdUrl = getUrlPattern().replace("{z}", String.valueOf(zoom))
						.replace("{x}", String.valueOf(x)).replace("{y}", String.valueOf(y));

				return new String[] { createdUrl };
			}

		};
		// setting a default Zoom level. which should be depends on 'max'
		info.setDefaultZoomLevel(defaultMax);

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
				return CustomTileMapServer.this.getAttributionText();
			}
		};
	}

}