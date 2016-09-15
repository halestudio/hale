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

import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileFactory;

import de.fhg.igd.mapviewer.MapPainter;

/**
 * Interface for map servers
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 * @version $Id$
 */
public interface MapServer {

	/**
	 * Set the map server's name
	 * 
	 * @param name the name
	 */
	public void setName(String name);

	/**
	 * Get the map server's name
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Get the tile factory
	 * 
	 * @param cache the tile cache to use
	 * 
	 * @return the tile factory
	 */
	public abstract TileFactory getTileFactory(TileCache cache);

	/**
	 * Get the overlay associated to the map
	 * 
	 * @return the painter for the map overlay, may be <code>null</code>
	 */
	public MapPainter getMapOverlay();

	/**
	 * Cleanup when the server is no longer used
	 */
	public abstract void cleanup();

}
