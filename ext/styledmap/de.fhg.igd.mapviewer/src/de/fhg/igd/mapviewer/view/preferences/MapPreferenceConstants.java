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
package de.fhg.igd.mapviewer.view.preferences;

import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileOverlayPainter;

import de.fhg.igd.mapviewer.MapPainter;
import de.fhg.igd.mapviewer.server.MapServer;

/**
 * Preference constants
 * 
 * @author Simon Templer
 */
public interface MapPreferenceConstants {

	/**
	 * The active {@link MapPainter}s
	 */
	public static final String ACTIVE_MAP_PAINTERS = "map.active.map_painters"; //$NON-NLS-1$

	/**
	 * The {@link TileCache} to use
	 */
	public static final String CACHE = "map.cache"; //$NON-NLS-1$

	/**
	 * The active {@link TileOverlayPainter}s
	 */
	public static final String ACTIVE_TILE_OVERLAYS = "map.active.tile_overlays"; //$NON-NLS-1$

	/**
	 * The current {@link MapServer}
	 */
	public static final String CURRENT_MAP_SERVER = "map.current.map_server"; //$NON-NLS-1$

}
