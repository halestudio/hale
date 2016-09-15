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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.GeotoolsConverter;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;
import org.jdesktop.swingx.mapviewer.PixelConverter;
import org.jdesktop.swingx.mapviewer.TileOverlayPainter;
import org.jdesktop.swingx.mapviewer.TileProviderUtils;

import de.fhg.igd.mapviewer.AbstractTileOverlayPainter;
import de.fhg.igd.mapviewer.MapKitTileOverlayPainter;
import de.fhg.igd.mapviewer.Refresher;
import de.fhg.igd.mapviewer.geom.BoundingBox;
import de.fhg.igd.mapviewer.geom.Verifier;
import de.fhg.igd.mapviewer.geom.indices.RTree;
import de.fhg.igd.mapviewer.marker.area.Area;

/**
 * CustomWaypointPainter
 * 
 * Based on the implementation of the WaypointPainter class
 *
 * @param <W> the way-point type
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 * @version $Id$
 */
public abstract class CustomWaypointPainter<W extends SelectableWaypoint<W>>
		extends MapKitTileOverlayPainter {

	private static final Log log = LogFactory.getLog(CustomWaypointPainter.class);

	private WaypointRenderer<W> renderer;

	private static final int PAGE_SIZE = 32;

	private final RTree<W> waypoints = new RTree<W>(PAGE_SIZE);

	private final Verifier<? super W, BoundingBox> matchTileVerifier = new Verifier<SelectableWaypoint<W>, BoundingBox>() {

		@Override
		public boolean verify(SelectableWaypoint<W> first, BoundingBox second) {
			return second.intersectsOrCovers(first.getBoundingBox());
		}
	};

	/**
	 * Way-points with a big area (bounding box) are painted first, selected
	 * way-points are painted last
	 */
	private final Comparator<? super W> paintFirstComparator = new Comparator<W>() {

		@Override
		public int compare(W o1, W o2) {
			// selected come last
			if (o1.isSelected() && !o2.isSelected()) {
				return 1;
			}
			else if (o2.isSelected() && !o1.isSelected()) {
				return -1;
			}
			else {
				double a1 = (o1.isPoint()) ? (0)
						: (o1.getBoundingBox().getWidth() * o1.getBoundingBox().getHeight());
				double a2 = (o2.isPoint()) ? (0)
						: (o2.getBoundingBox().getWidth() * o2.getBoundingBox().getHeight());

				int areaHint = 0;
				// compare size
				if (a1 > a2) {
					areaHint = -1;
				}
				else if (a2 > a1) {
					areaHint = 1;
				}

				return areaHint;
			}
		}

	};

	/**
	 * Creates a custom way-point painter that uses markers for painting
	 */
	public CustomWaypointPainter() {
		this(new MarkerWaypointRenderer<W>());
	}

	/**
	 * Creates a new instance of CustomWaypointPainter with one worker thread
	 * for painting tiles.
	 * 
	 * @param renderer the way-point renderer
	 */
	public CustomWaypointPainter(WaypointRenderer<W> renderer) {
		this(renderer, 1);
	}

	/**
	 * Creates a new instance of CustomWaypointPainter.
	 * 
	 * @param renderer the way-point renderer
	 * @param numberOfThreads the number of worker threads to use for painting
	 *            tiles
	 */
	public CustomWaypointPainter(WaypointRenderer<W> renderer, int numberOfThreads) {
		super(numberOfThreads);

		setRenderer(renderer);
	}

	/**
	 * Sets the way-point renderer to use when painting way-points
	 * 
	 * @param renderer the new CustomWaypointRenderer to use
	 */
	public void setRenderer(WaypointRenderer<W> renderer) {
		this.renderer = renderer;
	}

	/**
	 * Add a way-point
	 * 
	 * @param wp the way-point
	 * @param refresh the refresher
	 */
	public void addWaypoint(W wp, Refresher refresh) {
		BoundingBox bb = wp.getBoundingBox();

		if (bb != null) {
			synchronized (waypoints) {
				waypoints.insert(wp);
			}

			if (refresh != null) {
				wp.addToRefresher(refresh);
			}
		}
	}

	/**
	 * Remove a way-point
	 * 
	 * @param wp the way-point
	 * @param refresh the refresher
	 */
	public void removeWaypoint(W wp, Refresher refresh) {
		synchronized (waypoints) {
			waypoints.delete(wp);
		}

		if (refresh != null) {
			wp.addToRefresher(refresh);
		}
	}

	/**
	 * @see AbstractTileOverlayPainter#repaintTile(int, int, int, int,
	 *      PixelConverter, int)
	 */
	@Override
	public BufferedImage repaintTile(int posX, int posY, int width, int height,
			PixelConverter converter, int zoom) {
		if (renderer == null) {
			return null;
		}

		int overlap = getMaxOverlap();

		// overlap pixel coordinates
		Point topLeftPixel = new Point(Math.max(posX - overlap, 0), Math.max(posY - overlap, 0));
		Point bottomRightPixel = new Point(posX + width + overlap, posY + height + overlap); // TODO
																								// check
																								// against
																								// map
																								// size

		// overlap geo positions
		GeoPosition topLeft = converter.pixelToGeo(topLeftPixel, zoom);
		GeoPosition bottomRight = converter.pixelToGeo(bottomRightPixel, zoom);

		// overlap geo positions in RTree CRS
		try {
			BoundingBox tileBounds = createSearchBB(topLeft, bottomRight);

			synchronized (waypoints) {
				Set<W> candidates = waypoints.query(tileBounds, matchTileVerifier);

				if (candidates != null) {
					// sort way-points
					List<W> sorted = new ArrayList<W>(candidates);
					Collections.sort(sorted, paintFirstComparator);

					BufferedImage image = createImage(width, height);
					Graphics2D gfx = image.createGraphics();
					configureGraphics(gfx);

					try {
						// for each way-point within these bounds
						for (W w : sorted) {
							processWaypoint(w, posX, posY, width, height, converter, zoom, gfx);
						}

						/*
						 * DEBUG String test = getClass().getSimpleName() +
						 * " - x=" + posX + ", y=" + posY + ": " +
						 * candidates.size() + " WPs"; gfx.setColor(Color.BLUE);
						 * gfx.drawString(test, 4, height - 4);
						 * 
						 * gfx.drawString("minX: " + tileBounds.getMinX(), 4,
						 * height - 84); gfx.drawString("maxX: " +
						 * tileBounds.getMaxX(), 4, height - 64);
						 * gfx.drawString("minY: " + tileBounds.getMinY(), 4,
						 * height - 44); gfx.drawString("maxY: " +
						 * tileBounds.getMaxY(), 4, height - 24);
						 * 
						 * gfx.drawRect(0, 0, width - 1, height - 1);
						 */
					} finally {
						gfx.dispose();
					}

					return image;
				}
				else {
					return null;
				}
			}
		} catch (IllegalGeoPositionException e) {
			log.warn("Error painting waypoint tile: " + e.getMessage()); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * Create a search bounding box
	 * 
	 * @param topLeft the first geo-position
	 * @param bottomRight the second geo-position
	 * @return the bounding box
	 * 
	 * @throws IllegalGeoPositionException if a conversion fails
	 */
	private BoundingBox createSearchBB(GeoPosition topLeft, GeoPosition bottomRight)
			throws IllegalGeoPositionException {
		topLeft = GeotoolsConverter.getInstance().convert(topLeft, SelectableWaypoint.COMMON_EPSG);
		bottomRight = GeotoolsConverter.getInstance().convert(bottomRight,
				SelectableWaypoint.COMMON_EPSG);

		return new BoundingBox(Math.min(bottomRight.getX(), topLeft.getX()),
				Math.min(bottomRight.getY(), topLeft.getY()), -2.0,
				Math.max(bottomRight.getX(), topLeft.getX()),
				Math.max(bottomRight.getY(), topLeft.getY()), 2.0);
	}

	private void processWaypoint(W w, int minX, int minY, int width, int height,
			PixelConverter converter, int zoom, Graphics2D g) {
		try {
			Point2D point = converter.geoToPixel(w.getPosition(), zoom);
			int x = (int) (point.getX() - minX);
			int y = (int) (point.getY() - minY);
			PixelConverter converterWrapper = new TranslationPixelConverterDecorator(converter,
					(int) point.getX(), (int) point.getY());
			g.translate(x, y);
			Rectangle gBounds = new Rectangle(minX - (int) point.getX(), minY - (int) point.getY(),
					width, height);
			renderer.paintWaypoint(g, converterWrapper, zoom, w, gBounds);
			g.translate(-x, -y);
		} catch (IllegalGeoPositionException e) {
			// waypoint not in map bounds or position invalid
			// log.warn("Error painting waypoint", e);
		}
	}

	/**
	 * Find a way-point at a given position
	 * 
	 * @param point the position
	 * @return the way-point
	 */
	public W findWaypoint(Point point) {
		Rectangle viewPort = getMapKit().getMainMap().getViewportBounds();

		final int overlap = getMaxOverlap(); // the overlap is the reason why
												// the point is used instead of
												// a GeoPosition

		final int x = viewPort.x + point.x;
		final int y = viewPort.y + point.y;
		final int zoom = getMapKit().getMainMap().getZoom();
		final PixelConverter converter = getMapKit().getMainMap().getTileFactory().getTileProvider()
				.getConverter();

		final Dimension mapSize = TileProviderUtils
				.getMapSize(getMapKit().getMainMap().getTileFactory().getTileProvider(), zoom);
		final int width = mapSize.width
				* getMapKit().getMainMap().getTileFactory().getTileProvider().getTileWidth(zoom);
		final int height = mapSize.height
				* getMapKit().getMainMap().getTileFactory().getTileProvider().getTileHeight(zoom);

		final GeoPosition topLeft = converter
				.pixelToGeo(new Point(Math.max(x - overlap, 0), Math.max(y - overlap, 0)), zoom);
		final GeoPosition bottomRight = converter.pixelToGeo(
				new Point(Math.min(x + overlap, width), Math.min(y + overlap, height)), zoom);

		BoundingBox searchBox;
		try {
			searchBox = createSearchBB(topLeft, bottomRight);

			Set<W> wps = waypoints.query(searchBox, new Verifier<W, BoundingBox>() {

				@Override
				public boolean verify(W wp, BoundingBox box) {
					try {
						Point2D wpPixel = converter.geoToPixel(wp.getPosition(), zoom);

						int relX = x - (int) wpPixel.getX();
						int relY = y - (int) wpPixel.getY();

						Area area = wp.getMarker().getArea(zoom);
						if (area != null && area.contains(relX, relY)) {
							// match
							return true;
						}
					} catch (IllegalGeoPositionException e) {
						log.debug("Error converting waypoint position", e); //$NON-NLS-1$
					}

					return false;
				}
			});

			if (wps == null || wps.isEmpty()) {
				return null;
			}
			else {
				if (wps.size() == 1) {
					return wps.iterator().next();
				}
				else {
					List<W> sorted = new ArrayList<W>(wps);
					Collections.sort(sorted, new Comparator<W>() {

						@Override
						public int compare(W o1, W o2) {
							double a1 = o1.getMarker().getArea(zoom).getArea();
							double a2 = o2.getMarker().getArea(zoom).getArea();

							// compare size
							if (a1 < a2) {
								return -1;
							}
							else if (a2 < a1) {
								return 1;
							}
							else {
								return 0;
							}
						}

					});
					return sorted.get(0);
				}
			}
		} catch (IllegalGeoPositionException e) {
			return null;
		}
	}

	/**
	 * Find the way-points in a given rectangular area
	 * 
	 * @param rect the area
	 * @return the way-points in the area
	 */
	public Set<W> findWaypoints(Rectangle rect) {
		Rectangle viewPort = getMapKit().getMainMap().getViewportBounds();

		final Rectangle worldRect = new Rectangle(viewPort.x + rect.x, viewPort.y + rect.y,
				rect.width, rect.height);

		final int zoom = getMapKit().getMainMap().getZoom();
		final PixelConverter converter = getMapKit().getMainMap().getTileFactory().getTileProvider()
				.getConverter();

		final GeoPosition topLeft = converter.pixelToGeo(new Point(worldRect.x, worldRect.y), zoom);
		final GeoPosition bottomRight = converter.pixelToGeo(
				(new Point(worldRect.x + worldRect.width, worldRect.y + worldRect.height)), zoom);

		return findWaypoints(topLeft, bottomRight, worldRect, converter, zoom);
	}

	/**
	 * Find way-points in a rectangular area defined by the given
	 * {@link GeoPosition}s
	 * 
	 * @param topLeft the top left position
	 * @param bottomRight the bottom right position
	 * @param worldRect the bounding box in world pixel coordinates
	 * @param converter the pixel converter
	 * @param zoom the zoom level
	 * 
	 * @return the way-points in the area
	 */
	public Set<W> findWaypoints(GeoPosition topLeft, GeoPosition bottomRight,
			final Rectangle worldRect, final PixelConverter converter, final int zoom) {
		BoundingBox searchBox;
		try {
			searchBox = createSearchBB(topLeft, bottomRight);
			final BoundingBox verifyBox = searchBox;

			Set<W> wps = waypoints.query(searchBox, new Verifier<W, BoundingBox>() {

				@Override
				public boolean verify(W wp, BoundingBox second) {
					try {
						Point2D wpPixel = converter.geoToPixel(wp.getPosition(), zoom);
						int dx = (int) wpPixel.getX();
						int dy = (int) wpPixel.getY();

						worldRect.translate(-dx, -dy);
						try {
							Area area = wp.getMarker().getArea(zoom);
							if (area != null) {
								return area.containedIn(worldRect);
							}
							else {
								// something that has not been painted yet may
								// not be selected
								return false;
							}
						} finally {
							worldRect.translate(dx, dy);
						}
					} catch (IllegalGeoPositionException e) {
						log.warn("Could not convert waypoint position to pixel", e);
						// fall back to simple method
						return verifyBox.covers(wp.getBoundingBox());
					}
				}

			});

			if (wps == null) {
				return new HashSet<W>();
			}
			else {
				return wps;
			}
		} catch (IllegalGeoPositionException e) {
			return new HashSet<W>();
		}
	}

	/**
	 * Find the way-points in a given polygon
	 * 
	 * @param poly the polygon
	 * @return the way-points in the polygon area
	 */
	public Set<W> findWaypoints(final Polygon poly) {
		Rectangle viewPort = getMapKit().getMainMap().getViewportBounds();

		final int zoom = getMapKit().getMainMap().getZoom();
		final PixelConverter converter = getMapKit().getMainMap().getTileFactory().getTileProvider()
				.getConverter();

		de.fhg.igd.mapviewer.geom.Point2D[] points = new de.fhg.igd.mapviewer.geom.Point2D[poly.npoints];

		// create a metamodel polygon
		for (int i = 0; i < poly.npoints; i++) {
			int worldX = viewPort.x + poly.xpoints[i];
			int worldY = viewPort.y + poly.ypoints[i];

			// convert to geo position
			GeoPosition pos = converter.pixelToGeo(new Point(worldX, worldY), zoom);

			// convert to common CRS
			try {
				pos = GeotoolsConverter.getInstance().convert(pos, SelectableWaypoint.COMMON_EPSG);
			} catch (IllegalGeoPositionException e) {
				log.warn("Error converting polygon point for query"); //$NON-NLS-1$
				return new HashSet<W>();
			}

			points[i] = new de.fhg.igd.mapviewer.geom.Point2D(pos.getX(), pos.getY());
		}

		final de.fhg.igd.mapviewer.geom.shape.Polygon verifyPolygon = new de.fhg.igd.mapviewer.geom.shape.Polygon(
				points);

		// we need a 3D search bounding box for the R-Tree
		BoundingBox searchBox = verifyPolygon.getBoundingBox();
		searchBox.setMinZ(-2.0);
		searchBox.setMaxZ(2.0);

		poly.translate(viewPort.x, viewPort.y);
		try {
			Set<W> wps = waypoints.query(searchBox, new Verifier<W, BoundingBox>() {

				@Override
				public boolean verify(W wp, BoundingBox second) {
					try {
						Point2D wpPixel = converter.geoToPixel(wp.getPosition(), zoom);
						int dx = (int) wpPixel.getX();
						int dy = (int) wpPixel.getY();

						poly.translate(-dx, -dy);
						try {
							Area area = wp.getMarker().getArea(zoom);
							if (area != null) {
								return area.containedIn(poly);
							}
							else {
								// something that has not been painted yet may
								// not be selected
								return false;
							}
						} finally {
							poly.translate(dx, dy);
						}
					} catch (IllegalGeoPositionException e) {
						log.warn("Could not convert waypoint position to pixel", e);
						// fall back to simple method
						return verifyPolygon.contains(wp.getBoundingBox().toExtent());
					}
				}

			});

			if (wps == null) {
				return new HashSet<W>();
			}
			else {
				return wps;
			}
		} finally {
			poly.translate(-viewPort.x, -viewPort.y);
		}
	}

	/**
	 * Clear the way-points
	 */
	public void clearWaypoints() {
		synchronized (waypoints) {
			waypoints.flush();
		}

		refreshAll();
	}

	/**
	 * @see TileOverlayPainter#dispose()
	 */
	@Override
	public void dispose() {
		clearWaypoints();

		super.dispose();
	}

	/**
	 * Get the way-points bounding box.
	 * 
	 * @return the bounding box
	 */
	public BoundingBox getBoundingBox() {
		return waypoints.getRoot().getBoundingBox();
	}

}
