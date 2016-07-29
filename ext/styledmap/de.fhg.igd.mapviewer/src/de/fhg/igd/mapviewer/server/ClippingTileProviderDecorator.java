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
package de.fhg.igd.mapviewer.server;

import gnu.trove.TIntArrayList;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.mapviewer.AbstractTileProviderDecorator;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;
import org.jdesktop.swingx.mapviewer.JXMapViewer;
import org.jdesktop.swingx.mapviewer.PixelConverter;
import org.jdesktop.swingx.mapviewer.TileProvider;
import org.jdesktop.swingx.mapviewer.TileProviderUtils;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;

/**
 * ClippingTileProviderDecorator
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 */
public class ClippingTileProviderDecorator extends AbstractTileProviderDecorator {

	/**
	 * ClippingPainter
	 */
	public class ClippingPainter extends AbstractPainter<JXMapViewer> {

		private Color customOverlayColor;

		/**
		 * Default constructor
		 * 
		 * @param customOverlayColor a custom overlay color, may be
		 *            <code>null</code>
		 */
		public ClippingPainter(Color customOverlayColor) {
			setAntialiasing(true);
			setCacheable(false);
			this.customOverlayColor = customOverlayColor;
		}

		/**
		 * @see AbstractPainter#doPaint(Graphics2D, Object, int, int)
		 */
		@Override
		protected void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
			Rectangle viewport = map.getViewportBounds();
			final int zoom = map.getZoom();

			final int mapWidth = getMapWidthInTiles(zoom) * getTileWidth(zoom);
			final int mapHeight = getMapHeightInTiles(zoom) * getTileHeight(zoom);

			Point topLeft = getTopLeft(zoom);
			Point bottomRight = getBottomRight(zoom);

			Rectangle view = new Rectangle(topLeft);
			view.add(bottomRight);

			g.translate(-viewport.x, -viewport.y);
			Color back = map.getBackground();
			Color trans = (customOverlayColor != null) ? (customOverlayColor)
					: (new Color(back.getRed(), back.getGreen(), back.getBlue(), 90));
			g.setColor(map.getBackground());

			// draw view border
			if (viewport.intersects(view)) {
				g.draw(view);
			}

			// generate other rects
			List<Rectangle> rects = new ArrayList<Rectangle>();

			rects.add(new Rectangle(0, 0, topLeft.x, topLeft.y));
			rects.add(new Rectangle(topLeft.x, 0, bottomRight.x - topLeft.x, topLeft.y));
			rects.add(new Rectangle(bottomRight.x, 0, mapWidth - bottomRight.x, topLeft.y));

			rects.add(new Rectangle(0, topLeft.y, topLeft.x, bottomRight.y - topLeft.y));
			rects.add(new Rectangle(bottomRight.x, topLeft.y, mapWidth - bottomRight.x,
					bottomRight.y - topLeft.y));

			rects.add(new Rectangle(0, bottomRight.y, topLeft.x, mapHeight - bottomRight.y));
			rects.add(new Rectangle(topLeft.x, bottomRight.y, bottomRight.x - topLeft.x,
					mapHeight - bottomRight.y));
			rects.add(new Rectangle(bottomRight.x, bottomRight.y, mapWidth - bottomRight.x,
					mapHeight - bottomRight.y));

			g.setPaint(trans);

			for (Rectangle rect : rects) {
				if (viewport.intersects(rect))
					g.fill(rect);
			}

			g.translate(viewport.x, viewport.y);
		}

	}

	/**
	 * PixelConverter decorator that converts pixels conforming to the
	 * {@link ClippingTileProviderDecorator}'s map clipping
	 */
	public class PixelConverterDecorator implements PixelConverter {

		private final PixelConverter converter;

		/**
		 * Constructor
		 * 
		 * @param converter the internal pixel converter
		 */
		public PixelConverterDecorator(PixelConverter converter) {
			this.converter = converter;
		}

		/**
		 * @see PixelConverter#geoToPixel(GeoPosition, int)
		 */
		@Override
		public Point2D geoToPixel(final GeoPosition pos, final int zoom)
				throws IllegalGeoPositionException {
			Point2D result = converter.geoToPixel(pos, zoom);

			// move point (remove offset pixels)
			return new Point2D.Double(result.getX() - getXTileOffset(zoom) * getTileWidth(zoom),
					result.getY() - getYTileOffset(zoom) * getTileHeight(zoom));
		}

		/**
		 * @see PixelConverter#pixelToGeo(Point2D, int)
		 */
		@Override
		public GeoPosition pixelToGeo(final Point2D pixelCoordinate, final int zoom) {
			// move point (add offset pixels)
			Point2D moved = new Point2D.Double(
					pixelCoordinate.getX() + getXTileOffset(zoom) * getTileWidth(zoom),
					pixelCoordinate.getY() + getYTileOffset(zoom) * getTileHeight(zoom));

			return converter.pixelToGeo(moved, zoom);
		}

		/**
		 * @see PixelConverter#getMapEpsg()
		 */
		@Override
		public int getMapEpsg() {
			return converter.getMapEpsg();
		}

		/**
		 * @see PixelConverter#supportsBoundingBoxes()
		 */
		@Override
		public boolean supportsBoundingBoxes() {
			return converter.supportsBoundingBoxes();
		}

	}

	private static final Log log = LogFactory.getLog(ClippingTileProviderDecorator.class);

	private final TIntArrayList xTileOffset = new TIntArrayList();
	private final TIntArrayList xTileRange = new TIntArrayList();
	private final TIntArrayList yTileOffset = new TIntArrayList();
	private final TIntArrayList yTileRange = new TIntArrayList();

	private final ArrayList<Point> topLeft = new ArrayList<Point>();
	private final ArrayList<Point> bottomRight = new ArrayList<Point>();

	private final int maxZoom;

	private final Painter<JXMapViewer> painter;

	private PixelConverter lastConverter;
	private PixelConverterDecorator lastConverterDecorator;

	/**
	 * Constructor
	 * 
	 * @param tileProvider the tile provider
	 * @param topLeft the top left constraint
	 * @param bottomRight the bottom right constraint
	 */
	public ClippingTileProviderDecorator(final TileProvider tileProvider, final GeoPosition topLeft,
			final GeoPosition bottomRight) {
		this(tileProvider, topLeft, bottomRight, 1);
	}

	/**
	 * Constructor
	 * 
	 * @param tileProvider the tile provider
	 * @param topLeft the top left constraint
	 * @param bottomRight the bottom right constraint
	 * @param minRange the minimum visible range
	 */
	public ClippingTileProviderDecorator(final TileProvider tileProvider, final GeoPosition topLeft,
			final GeoPosition bottomRight, int minRange) {
		this(tileProvider, topLeft, bottomRight, minRange, null);
	}

	/**
	 * Constructor
	 * 
	 * @param tileProvider the tile provider
	 * @param topLeft the top left constraint
	 * @param bottomRight the bottom right constraint
	 * @param minRange the minimum visible range
	 * @param customOverlayColor custom overlay color to use, may be
	 *            <code>null</code>
	 */
	public ClippingTileProviderDecorator(final TileProvider tileProvider, final GeoPosition topLeft,
			final GeoPosition bottomRight, int minRange, Color customOverlayColor) {
		super(tileProvider);

		if (minRange <= 0)
			minRange = 1;

		int zoom = tileProvider.getMinimumZoom();
		boolean tryNextZoom = true;

		// determine valid zoom levels and their tile offsets/ranges
		while (tryNextZoom && zoom <= tileProvider.getMaximumZoom()) {
			try {
				Point2D topLeftPixel = tileProvider.getConverter().geoToPixel(topLeft, zoom);
				Point2D bottomRightPixel = tileProvider.getConverter().geoToPixel(bottomRight,
						zoom);

				int xMin = ((int) topLeftPixel.getX()) / tileProvider.getTileWidth(zoom);
				int yMin = ((int) topLeftPixel.getY()) / tileProvider.getTileHeight(zoom);
				int xMax = ((int) bottomRightPixel.getX()) / tileProvider.getTileWidth(zoom);
				int yMax = ((int) bottomRightPixel.getY()) / tileProvider.getTileHeight(zoom);

				// check for validity
				if (xMin <= xMax && yMin <= yMax
						&& TileProviderUtils.isValidTile(tileProvider, xMin, yMin, zoom)
						&& TileProviderUtils.isValidTile(tileProvider, xMax, yMax, zoom)) {
					// valid tiles, enter offset and ranges
					xTileOffset.add(xMin);
					xTileRange.add(xMax - xMin + 1);

					yTileOffset.add(yMin);
					yTileRange.add(yMax - yMin + 1);

					this.topLeft.add(new Point(
							(int) topLeftPixel.getX() - xMin * tileProvider.getTileWidth(zoom),
							(int) topLeftPixel.getY() - yMin * tileProvider.getTileHeight(zoom)));
					this.bottomRight.add(new Point(
							(int) bottomRightPixel.getX() - xMin * tileProvider.getTileWidth(zoom),
							(int) bottomRightPixel.getY()
									- yMin * tileProvider.getTileHeight(zoom)));

					if (xMax - xMin + 1 <= minRange || yMax - yMin + 1 <= minRange)
						tryNextZoom = false; // we reached the max zoom
					else
						zoom++; // prepare next zoom
				}
				else {
					// invalid tiles
					tryNextZoom = false;
					zoom--; // previous zoom
				}
			} catch (IllegalGeoPositionException e) {
				// invalid positions or conversion failed
				tryNextZoom = false;
				zoom--; // previous zoom
			}
		}

		if (zoom < getMinimumZoom()) {
			throw new IllegalArgumentException("No zoom levels are valid for clipping"); //$NON-NLS-1$
		}
		else {
			maxZoom = zoom;

			painter = new ClippingPainter(customOverlayColor);

			log.info("Initialized ClippingTileProviderDecorator with minZoom = " //$NON-NLS-1$
					+ tileProvider.getMinimumZoom() + ", maxZoom = " + maxZoom); //$NON-NLS-1$
		}
	}

	private int getXTileOffset(final int zoom) {
		int index = zoom - tileProvider.getMinimumZoom();
		if (index < 0 || index >= xTileOffset.size())
			throw new IllegalArgumentException("Illegal zoom value: " + zoom); //$NON-NLS-1$
		else
			return xTileOffset.get(index);
	}

	private int getXTileRange(final int zoom) {
		int index = zoom - tileProvider.getMinimumZoom();
		if (index < 0 || index >= xTileRange.size())
			throw new IllegalArgumentException("Illegal zoom value: " + zoom); //$NON-NLS-1$
		else
			return xTileRange.get(index);
	}

	private int getYTileOffset(final int zoom) {
		int index = zoom - tileProvider.getMinimumZoom();
		if (index < 0 || index >= yTileOffset.size())
			throw new IllegalArgumentException("Illegal zoom value: " + zoom); //$NON-NLS-1$
		else
			return yTileOffset.get(index);
	}

	private int getYTileRange(final int zoom) {
		int index = zoom - tileProvider.getMinimumZoom();
		if (index < 0 || index >= yTileRange.size())
			throw new IllegalArgumentException("Illegal zoom value: " + zoom); //$NON-NLS-1$
		else
			return yTileRange.get(index);
	}

	private Point getTopLeft(final int zoom) {
		int index = zoom - tileProvider.getMinimumZoom();
		if (index < 0 || index >= topLeft.size())
			throw new IllegalArgumentException("Illegal zoom value: " + zoom); //$NON-NLS-1$
		else
			return topLeft.get(index);
	}

	private Point getBottomRight(final int zoom) {
		int index = zoom - tileProvider.getMinimumZoom();
		if (index < 0 || index >= bottomRight.size())
			throw new IllegalArgumentException("Illegal zoom value: " + zoom); //$NON-NLS-1$
		else
			return bottomRight.get(index);
	}

	/**
	 * @see AbstractTileProviderDecorator#getConverter()
	 */
	@Override
	public PixelConverter getConverter() {
		PixelConverter converter = super.getConverter();

		if (converter == null) {
			return converter;
		}
		else if (lastConverterDecorator != null && converter == lastConverter) {
			return lastConverterDecorator;
		}
		else {
			lastConverter = converter;
			lastConverterDecorator = new PixelConverterDecorator(converter);
			return lastConverterDecorator;
		}
	}

	/**
	 * @see AbstractTileProviderDecorator#getDefaultZoom()
	 */
	@Override
	public int getDefaultZoom() {
		int zoom = super.getDefaultZoom();
		if (zoom > maxZoom)
			return maxZoom;
		else
			return zoom;
	}

	/**
	 * @see AbstractTileProviderDecorator#getMapHeightInTiles(int)
	 */
	@Override
	public int getMapHeightInTiles(int zoom) {
		return getYTileRange(zoom);
	}

	/**
	 * @see AbstractTileProviderDecorator#getMapWidthInTiles(int)
	 */
	@Override
	public int getMapWidthInTiles(int zoom) {
		return getXTileRange(zoom);
	}

	/**
	 * @see AbstractTileProviderDecorator#getMaximumZoom()
	 */
	@Override
	public int getMaximumZoom() {
		return maxZoom;
	}

	/**
	 * @see AbstractTileProviderDecorator#getTileUris(int, int, int)
	 */
	@Override
	public URI[] getTileUris(int x, int y, int zoom) {
		try {
			return super.getTileUris(x + getXTileOffset(zoom), y + getYTileOffset(zoom), zoom);
		} catch (Exception e) {
			log.error("Error getting tile uris", e); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * @see AbstractTileProviderDecorator#getTotalMapZoom()
	 */
	@Override
	public int getTotalMapZoom() {
		return maxZoom;
	}

	/**
	 * @see TileProvider#getMapOverlayPainter()
	 */
	@Override
	public Painter<JXMapViewer> getMapOverlayPainter() {
		return painter;
	}

}
