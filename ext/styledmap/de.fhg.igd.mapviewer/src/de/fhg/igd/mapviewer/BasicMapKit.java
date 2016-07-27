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

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.mapviewer.DefaultTileCache;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.GeotoolsConverter;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;
import org.jdesktop.swingx.mapviewer.JXMapKit;
import org.jdesktop.swingx.mapviewer.JXMapViewer;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileOverlayPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.Painter;
import org.springframework.util.ClassUtils;

import de.fhg.igd.mapviewer.concurrency.Concurrency;
import de.fhg.igd.mapviewer.concurrency.IJob;
import de.fhg.igd.mapviewer.concurrency.Job;
import de.fhg.igd.mapviewer.concurrency.Progress;
import de.fhg.igd.mapviewer.concurrency.SwingCallback;
import de.fhg.igd.mapviewer.server.MapServer;

/**
 * Basic MapKit
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 */
public class BasicMapKit extends JXMapKit {

	private static final Log log = LogFactory.getLog(BasicMapKit.class);

	private static final long serialVersionUID = -708805464636342709L;

	/**
	 * The painter for the current map tool
	 */
	private final MapToolPainter toolPainter;

	/**
	 * The current map server
	 */
	private MapServer server;

	private TileCache cache;

	/**
	 * Defines whether this is synced with the 3D camera
	 */
	private boolean synced;

	/**
	 * The painter for all overlays
	 */
	private final CompoundPainter<JXMapViewer> painter;

	/**
	 * The custom painters
	 */
	private final CompoundPainter<JXMapViewer> customPainter;

	/**
	 * The painters associated directly with the map
	 * 
	 * @see MapServer#getMapOverlay()
	 */
	private final CompoundPainter<JXMapViewer> mapPainter;

	private List<MapPainter> customPainters = new ArrayList<MapPainter>();

	private Lock customPaintersLock = new ReentrantLock();

	/**
	 * Creates a basic map kit
	 */
	public BasicMapKit() {
		this(new DefaultTileCache());
	}

	/**
	 * Creates a basic map kit
	 * 
	 * @param cache the tile cache to use
	 */
	public BasicMapKit(TileCache cache) {
		super();

		this.cache = cache;

		getMiniMap().setPanEnabled(false);
		getMiniMap().setCursor(Cursor.getDefaultCursor());

		getZoomSlider().setCursor(Cursor.getDefaultCursor());
		getZoomInButton().setCursor(Cursor.getDefaultCursor());
		getZoomOutButton().setCursor(Cursor.getDefaultCursor());

		getMiniMap().addMouseListener(new MouseAdapter() {

			/**
			 * @see MouseAdapter#mouseClicked(MouseEvent)
			 */
			@Override
			public void mouseClicked(MouseEvent me) {
				getMainMap()
						.setCenterPosition(getMiniMap().convertPointToGeoPosition(me.getPoint()));
			}

		});

		// create painter for map tools
		toolPainter = new MapToolPainter(getMainMap());

		customPainter = new CompoundPainter<JXMapViewer>();
		customPainter.setCacheable(false);

		mapPainter = new CompoundPainter<JXMapViewer>();
		mapPainter.setCacheable(false);

		painter = new CompoundPainter<JXMapViewer>();
		painter.setPainters(customPainter, toolPainter, mapPainter);
		painter.setCacheable(false);

		updatePainters();

		// register as state provider
		// GuiState.getInstance().registerStateProvider(this);
	}

	/**
	 * Update the custom painters
	 */
	private void updatePainters() {
		customPaintersLock.lock();
		try {
			for (MapPainter painter : customPainters) {
				painter.setMapKit(this);
			}

			MapPainter[] painters = new MapPainter[customPainters.size()];
			customPainters.toArray(painters);
			customPainter.setPainters(painters);
		} finally {
			customPaintersLock.unlock();
		}
	}

	/**
	 * Get all custom painters of a given type
	 * 
	 * @param type the painter type
	 * @param <T> the painter type
	 * @return the list of custom painters (not backed by the map kit)
	 */
	@SuppressWarnings("unchecked")
	public <T extends MapPainter> List<T> getCustomPainters(Class<T> type) {
		List<T> results = new ArrayList<T>();

		customPaintersLock.lock();
		try {
			for (MapPainter painter : customPainters) {
				if (ClassUtils.isAssignable(type, painter.getClass())) {
					results.add((T) painter);
				}
			}
		} finally {
			customPaintersLock.unlock();
		}

		return results;
	}

	/**
	 * @return the list of custom painters (not backed by the map kit)
	 */
	public List<MapPainter> getCustomPainters() {
		List<MapPainter> results;

		customPaintersLock.lock();
		try {
			results = new ArrayList<MapPainter>(customPainters);
		} finally {
			customPaintersLock.unlock();
		}

		return results;
	}

	/**
	 * Sets the custom painters
	 * 
	 * @param customPainters the custom painters
	 */
	public void setCustomPainters(List<MapPainter> customPainters) {
		customPaintersLock.lock();
		try {
			this.customPainters = new ArrayList<MapPainter>(customPainters);
		} finally {
			customPaintersLock.unlock();
		}

		updatePainters();
	}

	/**
	 * Adds a custom map painter
	 * 
	 * @param painter the map painter
	 */
	public void addCustomPainter(MapPainter painter) {
		synchronized (customPainters) {
			customPainters.add(painter);
		}

		updatePainters();
	}

	/**
	 * Removes a custom map painter
	 * 
	 * @param painter the map painter
	 */
	public void removeCustomPainter(MapPainter painter) {
		synchronized (customPainters) {
			customPainters.remove(painter);
		}

		updatePainters();
	}

	/**
	 * Get all tile overlay painters of a certain type
	 * 
	 * @param <T> the painter type
	 * @param type the painter type
	 * @return the list of tile overlay painters
	 */
	@SuppressWarnings("unchecked")
	public <T extends TileOverlayPainter> List<T> getTilePainters(Class<T> type) {
		List<T> results = new ArrayList<T>();

		for (TileOverlayPainter painter : getMainMap().getTileOverlays()) {
			if (ClassUtils.isAssignable(type, painter.getClass())) {
				results.add((T) painter);
			}
		}

		return results;
	}

	/**
	 * Creates all map painters save the tool painter
	 * 
	 * @param mapViewer the main map viewer
	 * 
	 * @return a list of painters
	 */
	protected List<Painter<JXMapViewer>> createPainters(JXMapViewer mapViewer) {
		return new ArrayList<Painter<JXMapViewer>>();
	}

	/**
	 * Set the current map tool
	 * 
	 * @param tool the {@link MapTool} to use
	 */
	public void setMapTool(MapTool tool) {
		if (toolPainter.getMapTool() != null)
			toolPainter.getMapTool().setActive(false);
		toolPainter.setMapTool(tool);
		tool.setActive(true);
		getMainMap().setPanEnabled(tool.isPanEnabled());
		getMainMap().setCursor(tool.getCursor());
		getMainMap().repaint();
	}

	/**
	 * Get the current map tool
	 * 
	 * @return the current map tool
	 */
	public MapTool getMapTool() {
		return toolPainter.getMapTool();
	}

	/**
	 * Set the map kit's map server
	 * 
	 * @param server the map server
	 * @param skipZoom if zooming to the area visible in the last map shall be
	 *            skipped (makes sense when instead loading the state from file)
	 */
	public void setServer(final MapServer server, final boolean skipZoom) {
		this.server = server;

		// remember map area
		final Set<GeoPosition> gps = new HashSet<GeoPosition>();
		gps.add(getMainMap().convertPointToGeoPosition(
				new Point((int) Math.round(getMainMap().getWidth() * 0.1),
						(int) Math.round(getMainMap().getHeight() * 0.1))));
		gps.add(getMainMap().convertPointToGeoPosition(
				new Point((int) Math.round(getMainMap().getWidth() * 0.9),
						(int) Math.round(getMainMap().getHeight() * 0.9))));

		onChangingServer(server);

		IJob<Void> job = new Job<Void>(Messages.BasicMapKit_0, new SwingCallback<Void>() {

			@Override
			protected void finished(Void result) {
				// dispose old map overlay
				Painter<?>[] oldOverlays = mapPainter.getPainters();
				if (oldOverlays != null) {
					for (Painter<?> oldOverlay : oldOverlays) {
						if (oldOverlay instanceof MapPainter) {
							((MapPainter) oldOverlay).dispose();
						}
					}
				}

				// set the new map overlay
				MapPainter mapOverlay = server.getMapOverlay();
				if (mapOverlay != null) {
					mapPainter.setPainters(mapOverlay);
					mapOverlay.setMapKit(BasicMapKit.this);
				}
				else {
					mapPainter.setPainters();
				}
				getMainMap().setOverlayPainter(painter);

				if (!skipZoom) {
					// done in the step below - setCenterPosition(pos);
					zoomToPositions(gps);
				}

				revalidate();
			}

			@Override
			protected void error(Throwable e) {
				log.error("Error configuring map", e); //$NON-NLS-1$
			}

		}) {

			@Override
			public Void work(Progress progress) throws Exception {
				// configure the map kit
				BasicMapKit.this.setTileFactory(server.getTileFactory(cache));

				return null;
			}

		};
		Concurrency.startJob(job);
	}

	/**
	 * Set the tile cache
	 * 
	 * @param cache the tile cache to use
	 */
	public void setTileCache(TileCache cache) {
		this.cache = cache;

		// re-set current map
		MapServer server = getServer();
		setServer(server, false);
	}

	/**
	 * Called just before the current map server is set to the given server
	 * 
	 * @param server the new map server
	 */
	protected void onChangingServer(MapServer server) {
		// override me
	}

	/**
	 * @see JXMapKit#setZoom(int)
	 */
	@Override
	public void setZoom(int zoom) {
		zoom = Math.min(zoom, getMainMap().getTileFactory().getTileProvider().getMaximumZoom());
		super.setZoom(zoom);

		if (zoom + 4 > getMainMap().getTileFactory().getTileProvider().getMaximumZoom())
			setMiniMapVisible(false);
		else
			setMiniMapVisible(true);
	}

	/**
	 * Refresh the map
	 */
	public void refresh() {
		getMainMap().repaint();
	}

	/**
	 * Zoom in and center on the given {@link GeoPosition}
	 * 
	 * @param pos the {@link GeoPosition}
	 */
	public void zoomInToPosition(GeoPosition pos) {
		setZoom(getMainMap().getTileFactory().getTileProvider().getMinimumZoom());
		setCenterPosition(pos);
	}

	private static Set<GeoPosition> equalizeEpsg(Collection<GeoPosition> positions) {
		if (positions.isEmpty())
			return new HashSet<GeoPosition>(positions);

		int epsg = -1;

		Set<GeoPosition> result = new HashSet<GeoPosition>();

		for (GeoPosition pos : positions) {
			if (epsg == -1) {
				epsg = pos.getEpsgCode();
				result.add(pos);
			}
			else if (epsg != pos.getEpsgCode()) {
				GeoPosition altPos;
				try {
					altPos = GeotoolsConverter.getInstance().convert(pos, epsg);
					result.add(altPos);
				} catch (IllegalGeoPositionException e) {
					log.warn("Error converting GeoPosition, ignoring this position"); //$NON-NLS-1$
				}
			}
			else {
				result.add(pos);
			}
		}

		return result;
	}

	/**
	 * Center on the given {@link GeoPosition}s
	 * 
	 * @param positions the {@link GeoPosition}s
	 */
	public void centerOnPositions(Set<GeoPosition> positions) {
		if (positions.size() == 0)
			return;

		positions = equalizeEpsg(positions);

		double minX = 0, maxX = 0, minY = 0, maxY = 0;

		boolean init = false;
		int epsg = positions.iterator().next().getEpsgCode();

		for (GeoPosition pos : positions) {
			if (!init) {
				// first pos
				minY = maxY = pos.getY();
				minX = maxX = pos.getX();

				init = true;
			}
			else {
				if (pos.getY() < minY)
					minY = pos.getY();
				else if (pos.getY() > maxY)
					maxY = pos.getY();

				if (pos.getX() < minX)
					minX = pos.getX();
				else if (pos.getX() > maxX)
					maxX = pos.getX();
			}
		}

		setCenterPosition(new GeoPosition((minX + maxX) / 2.0, (minY + maxY) / 2.0, epsg));
	}

	private Rectangle2D generateBoundingRect(double minX, double minY, double maxX, double maxY,
			int epsg, int zoom) throws IllegalGeoPositionException {
		java.awt.geom.Point2D p1 = getMainMap().getTileFactory().getTileProvider().getConverter()
				.geoToPixel(new GeoPosition(minX, minY, epsg), zoom);
		java.awt.geom.Point2D p2 = getMainMap().getTileFactory().getTileProvider().getConverter()
				.geoToPixel(new GeoPosition(maxX, maxY, epsg), zoom);

		return new Rectangle2D.Double((p1.getX() < p2.getX()) ? (p1.getX()) : (p2.getX()),
				(p1.getY() < p2.getY()) ? (p1.getY()) : (p2.getY()),
				Math.abs(p2.getX() - p1.getX()), Math.abs(p2.getY() - p1.getY()));
	}

	/**
	 * Zoom in and center on the given {@link GeoPosition}s
	 * 
	 * @param positions the {@link GeoPosition}s
	 */
	public void zoomToPositions(Set<GeoPosition> positions) {
		if (positions.size() == 0)
			return;

		positions = equalizeEpsg(positions);
		int epsg = positions.iterator().next().getEpsgCode();

		double minX = 0, maxX = 0, minY = 0, maxY = 0;

		boolean init = false;

		for (GeoPosition pos : positions) {
			if (!init) {
				// first pos
				minY = maxY = pos.getY();
				minX = maxX = pos.getX();

				init = true;
			}
			else {
				if (pos.getY() < minY)
					minY = pos.getY();
				else if (pos.getY() > maxY)
					maxY = pos.getY();

				if (pos.getX() < minX)
					minX = pos.getX();
				else if (pos.getX() > maxX)
					maxX = pos.getX();
			}
		}

		// center on positions
		setCenterPosition(new GeoPosition((minX + maxX) / 2.0, (minY + maxY) / 2.0, epsg));

		// initial zoom
		int zoom = getMainMap().getTileFactory().getTileProvider().getMinimumZoom();

		try {
			if (positions.size() >= 2) {
				int viewWidth = (int) getMainMap().getViewportBounds().getWidth();
				int viewHeight = (int) getMainMap().getViewportBounds().getHeight();

				Rectangle2D rect = generateBoundingRect(minX, minY, maxX, maxY, epsg, zoom);

				while ((viewWidth < rect.getWidth() || viewHeight < rect.getHeight())
						&& zoom < getMainMap().getTileFactory().getTileProvider()
								.getMaximumZoom()) {
					zoom++;
					rect = generateBoundingRect(minX, minY, maxX, maxY, epsg, zoom);
				}
			}

			setZoom(zoom);
		} catch (IllegalGeoPositionException e) {
			log.warn("Error zooming to positions");// , e); //$NON-NLS-1$
		}
	}

	/**
	 * @return the server
	 */
	public MapServer getServer() {
		return server;
	}

	/**
	 * @param synced true if synced with the 3D map camera, false otherwise
	 */
	public void setSynced(boolean synced) {
		this.synced = synced;
	}

	/**
	 * @return true if synced with the 3D map camera, false otherwise
	 */
	public boolean isSynced() {
		return this.synced;
	}

	/*
	 * protected void loadState(GuiState state) { int oldEpsg =
	 * state.getInteger(BasicMapKit.class, LAST_EPSG, DEF_EPSG);
	 * 
	 * GeoPosition p1 = new GeoPosition(state.getDouble(BasicMapKit.class,
	 * SHOW_MIN_X, DEF_MIN_X), state.getDouble(BasicMapKit.class, SHOW_MIN_Y,
	 * DEF_MIN_Y), oldEpsg); GeoPosition p2 = new
	 * GeoPosition(state.getDouble(BasicMapKit.class, SHOW_MAX_X, DEF_MAX_X),
	 * state.getDouble(BasicMapKit.class, SHOW_MAX_Y, DEF_MAX_Y), oldEpsg);
	 * 
	 * Set<GeoPosition> positions = new HashSet<GeoPosition>();
	 * positions.add(p1); positions.add(p2); zoomToPositions(positions); }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fhg.igd.mutable.gui.StateProvider#onStateSave(de.fhg.igd.mutable.gui.
	 * GuiState)
	 */
	/*
	 * @Override public void onStateSave(GuiState state) { GeoPosition min =
	 * getMainMap().convertPointToGeoPosition(new Point((int)
	 * Math.round(getMainMap().getWidth() * 0.1), (int)
	 * Math.round(getMainMap().getHeight() * 0.1))); GeoPosition max =
	 * getMainMap().convertPointToGeoPosition(new Point((int)
	 * Math.round(getMainMap().getWidth() * 0.9), (int)
	 * Math.round(getMainMap().getHeight() * 0.9)));
	 * 
	 * //GeoPosition min = getMainMap().convertPointToGeoPosition(new Point(0,
	 * 0)); //GeoPosition max = getMainMap().convertPointToGeoPosition(new
	 * Point(getMainMap().getWidth(), getMainMap().getHeight()));
	 * 
	 * int epsg = min.getEpsgCode();
	 * 
	 * try { if (epsg != max.getEpsgCode()) max =
	 * EpsgGeoConverter.INSTANCE.convert(max, epsg);
	 * 
	 * state.setDouble(BasicMapKit.class, SHOW_MIN_Y, min.getY());
	 * state.setDouble(BasicMapKit.class, SHOW_MIN_X, min.getX());
	 * state.setDouble(BasicMapKit.class, SHOW_MAX_Y, max.getY());
	 * state.setDouble(BasicMapKit.class, SHOW_MAX_X, max.getX());
	 * 
	 * state.setInteger(BasicMapKit.class, LAST_EPSG, epsg); } catch
	 * (IllegalGeoPositionException e) { log.error(
	 * "Error saving map state: Could not convert GeoPosition", e); } }
	 */

	/*
	 * @Override public void guiClosing() { // do nothing }
	 */

	/*
	 * @Override public void guiStarted() { loadState(GuiState.getInstance()); }
	 */

}
