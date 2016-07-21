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
package de.fhg.igd.mapviewer.server.wms;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.empty.EmptyTileFactory;

import de.fhg.igd.mapviewer.MapPainter;
import de.fhg.igd.mapviewer.server.CustomTileFactory;
import de.fhg.igd.mapviewer.server.MapServer;

/**
 * WMSMapServer
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class WMSMapServer extends WMSTileConfiguration implements MapServer {

	private static final Log log = LogFactory.getLog(WMSMapServer.class);

	private TileFactory factory = null;
	private WMSTileProvider provider = null;

	/**
	 * The preferences
	 */
	private static final Preferences PREF_SERVERS = Preferences
			.userNodeForPackage(WMSMapServer.class).node("servers"); //$NON-NLS-1$

	/**
	 * @see MapServer#cleanup()
	 */
	@Override
	public void cleanup() {
		if (factory != null)
			factory.cleanup();
	}

	/**
	 * @see MapServer#getMapOverlay()
	 */
	@Override
	public MapPainter getMapOverlay() {
		return null;
	}

	/**
	 * @see MapServer#getTileFactory(TileCache)
	 */
	@Override
	public TileFactory getTileFactory(TileCache cache) {
		try {
			provider = new WMSTileProvider(getBaseUrl(), getPreferredEpsg(), getZoomLevels(),
					getMinTileSize(), getMinMapSize(), getLayers());
			factory = new CustomTileFactory(provider, cache);
		} catch (Exception e) {
			log.error("Error creating wms tile provider", e); //$NON-NLS-1$
			factory = new EmptyTileFactory();
			provider = null;
		}

		return factory;
	}

	/**
	 * @see WMSConfiguration#getPreferences()
	 */
	@Override
	public Preferences getPreferences() {
		return PREF_SERVERS;
	}

	/**
	 * Remove the configuration with the given name
	 * 
	 * @param name the name
	 * 
	 * @return if removing the configuration succeeded
	 */
	public static boolean removeConfiguration(String name) {
		try {
			PREF_SERVERS.node(name).removeNode();
			return true;
		} catch (BackingStoreException e) {
			log.error("Error removing configuration " + name, e); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * Get the names of the existing configurations
	 * 
	 * @return the configuration names
	 */
	public static String[] getConfigurationNames() {
		try {
			return PREF_SERVERS.childrenNames();
		} catch (BackingStoreException e) {
			return new String[] {};
		}
	}

}
