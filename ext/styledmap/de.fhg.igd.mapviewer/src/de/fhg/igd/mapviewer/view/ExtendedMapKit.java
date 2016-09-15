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
package de.fhg.igd.mapviewer.view;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistable;
import org.eclipse.ui.IPersistableEditor;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.GeotoolsConverter;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileOverlayPainter;

import de.fhg.igd.eclipse.util.extension.exclusive.ExclusiveExtension.ExclusiveExtensionListener;
import de.fhg.igd.eclipse.util.extension.selective.SelectiveExtension.SelectiveExtensionListener;
import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.MapKitTileOverlayPainter;
import de.fhg.igd.mapviewer.MapPainter;
import de.fhg.igd.mapviewer.MapTool;
import de.fhg.igd.mapviewer.server.MapServer;
import de.fhg.igd.mapviewer.server.MapServerFactory;
import de.fhg.igd.mapviewer.view.cache.ITileCacheFactory;
import de.fhg.igd.mapviewer.view.cache.ITileCacheService;
import de.fhg.igd.mapviewer.view.overlay.IMapPainterService;
import de.fhg.igd.mapviewer.view.overlay.ITileOverlayService;
import de.fhg.igd.mapviewer.view.overlay.MapPainterFactory;
import de.fhg.igd.mapviewer.view.overlay.TileOverlayFactory;
import de.fhg.igd.mapviewer.view.server.IMapServerService;

/**
 * Map kit that stores its state in an {@link IMemento} and uses the
 * {@link IMapPainterService} and {@link ITileOverlayService} to determine the
 * active overlays.
 * 
 * @author Simon Templer
 */
public class ExtendedMapKit extends BasicMapKit implements IPersistableEditor {

	private static final String MEMENTO_KEY_MAX_Y = "max_y"; //$NON-NLS-1$
	private static final String MEMENTO_KEY_MAX_X = "max_x"; //$NON-NLS-1$
	private static final String MEMENTO_KEY_MIN_Y = "min_y"; //$NON-NLS-1$
	private static final String MEMENTO_KEY_MIN_X = "min_x"; //$NON-NLS-1$
	private static final String MEMENTO_KEY_EPSG = "epsg"; //$NON-NLS-1$

	private static final long serialVersionUID = -764275511553180773L;

	private static final Log log = LogFactory.getLog(ExtendedMapKit.class);

	private final AbstractMapView view;

	/**
	 * Constructor
	 * 
	 * @param view the map view
	 */
	public ExtendedMapKit(final AbstractMapView view) {
		super(getCache());

		this.view = view;

		// tile cache
		ITileCacheService cacheService = PlatformUI.getWorkbench()
				.getService(ITileCacheService.class);
		cacheService.addListener(new ExclusiveExtensionListener<TileCache, ITileCacheFactory>() {

			@Override
			public void currentObjectChanged(TileCache current, ITileCacheFactory definition) {
				setTileCache(current);
			}

		});

		// map painters
		IMapPainterService mapPainters = PlatformUI.getWorkbench()
				.getService(IMapPainterService.class);
		List<MapPainter> painterList = new ArrayList<MapPainter>();
		for (MapPainter mapPainter : mapPainters.getActiveObjects()) {
			painterList.add(mapPainter);
		}
		setCustomPainters(painterList);

		mapPainters.addListener(new SelectiveExtensionListener<MapPainter, MapPainterFactory>() {

			@Override
			public void deactivated(MapPainter object, MapPainterFactory definition) {
				removeCustomPainter(object);
			}

			@Override
			public void activated(MapPainter object, MapPainterFactory definition) {
				addCustomPainter(object);
			}
		});

		// tile overlays
		ITileOverlayService tileOverlays = PlatformUI.getWorkbench()
				.getService(ITileOverlayService.class);
		SortedSet<TileOverlayPainter> mainOverlays = new TreeSet<TileOverlayPainter>();
		SortedSet<TileOverlayPainter> miniOverlays = new TreeSet<TileOverlayPainter>();
		for (TileOverlayPainter tileOverlay : tileOverlays.getActiveObjects()) {
			if (tileOverlay instanceof MapKitTileOverlayPainter) {
				((MapKitTileOverlayPainter) tileOverlay).setMapKit(this);
			}

			mainOverlays.add(tileOverlay);

			if (tileOverlays.getDefinition(tileOverlay).showInMiniMap()) {
				miniOverlays.add(tileOverlay);
			}
		}
		getMainMap().setTileOverlays(mainOverlays);
		getMiniMap().setTileOverlays(miniOverlays);

		tileOverlays.addListener(
				new SelectiveExtensionListener<TileOverlayPainter, TileOverlayFactory>() {

					@Override
					public void deactivated(TileOverlayPainter object,
							TileOverlayFactory definition) {
						getMainMap().removeTileOverlay(object);
						if (definition.showInMiniMap()) {
							getMiniMap().removeTileOverlay(object);
						}
					}

					@Override
					public void activated(TileOverlayPainter object,
							TileOverlayFactory definition) {
						if (object instanceof MapKitTileOverlayPainter) {
							((MapKitTileOverlayPainter) object).setMapKit(ExtendedMapKit.this);
						}

						getMainMap().addTileOverlay(object);
						if (definition.showInMiniMap()) {
							getMiniMap().addTileOverlay(object);
						}
					}
				});

		// map server
		IMapServerService mapServers = PlatformUI.getWorkbench()
				.getService(IMapServerService.class);
		setServer(mapServers.getCurrent(), true);

		mapServers.addListener(new ExclusiveExtensionListener<MapServer, MapServerFactory>() {

			@Override
			public void currentObjectChanged(MapServer current, MapServerFactory definition) {
				setServer(current, false);
			}
		});
	}

	/**
	 * Get the current tile cache
	 * 
	 * @return the current tile cache
	 */
	private static TileCache getCache() {
		ITileCacheService cacheService = PlatformUI.getWorkbench()
				.getService(ITileCacheService.class);
		return cacheService.getCurrent();
	}

	/**
	 * @see BasicMapKit#setMapTool(MapTool)
	 */
	@Override
	public void setMapTool(MapTool tool) {
		ISelectionService service = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getSelectionService();

		MapTool oldTool = getMapTool();
		if (oldTool != null && oldTool instanceof ISelectionListener) {
			service.removeSelectionListener((ISelectionListener) oldTool);
		}

		super.setMapTool(tool);

		if (tool instanceof ISelectionListener) {
			service.addSelectionListener((ISelectionListener) tool);
		}

		// set the tool as selection provider
		if (tool instanceof ISelectionProvider) {
			view.setSelectionProvider((ISelectionProvider) tool);
		}
		else {
			view.setSelectionProvider(null);
		}
	}

	/**
	 * @see IPersistableEditor#restoreState(IMemento)
	 */
	@Override
	public void restoreState(IMemento memento) {
		if (memento == null)
			return;

		Integer oldEpsg = memento.getInteger(MEMENTO_KEY_EPSG);
		if (oldEpsg != null) {
			GeoPosition p1 = new GeoPosition(memento.getFloat(MEMENTO_KEY_MIN_X),
					memento.getFloat(MEMENTO_KEY_MIN_Y), oldEpsg);
			GeoPosition p2 = new GeoPosition(memento.getFloat(MEMENTO_KEY_MAX_X),
					memento.getFloat(MEMENTO_KEY_MAX_Y), oldEpsg);

			Set<GeoPosition> positions = new HashSet<GeoPosition>();
			positions.add(p1);
			positions.add(p2);
			zoomToPositions(positions);
		}
	}

	/**
	 * @see IPersistable#saveState(IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		GeoPosition min = getMainMap().convertPointToGeoPosition(
				new Point((int) Math.round(getMainMap().getWidth() * 0.1),
						(int) Math.round(getMainMap().getHeight() * 0.1)));
		GeoPosition max = getMainMap().convertPointToGeoPosition(
				new Point((int) Math.round(getMainMap().getWidth() * 0.9),
						(int) Math.round(getMainMap().getHeight() * 0.9)));

		// GeoPosition min = getMainMap().convertPointToGeoPosition(new Point(0,
		// 0));
		// GeoPosition max = getMainMap().convertPointToGeoPosition(new
		// Point(getMainMap().getWidth(), getMainMap().getHeight()));

		int epsg = min.getEpsgCode();

		try {
			if (epsg != max.getEpsgCode())
				max = GeotoolsConverter.getInstance().convert(max, epsg);

			memento.putFloat(MEMENTO_KEY_MIN_X, (float) min.getX());
			memento.putFloat(MEMENTO_KEY_MIN_Y, (float) min.getY());
			memento.putFloat(MEMENTO_KEY_MAX_X, (float) max.getX());
			memento.putFloat(MEMENTO_KEY_MAX_Y, (float) max.getY());

			memento.putInteger(MEMENTO_KEY_EPSG, epsg);
		} catch (IllegalGeoPositionException e) {
			log.error("Error saving map state: Could not convert GeoPosition", e); //$NON-NLS-1$
		}
	}

}
