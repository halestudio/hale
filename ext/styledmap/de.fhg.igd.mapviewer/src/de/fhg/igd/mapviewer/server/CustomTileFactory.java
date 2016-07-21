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
import org.jdesktop.swingx.mapviewer.TileProvider;

/**
 * CustomTileFactory
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class CustomTileFactory extends StatusTileFactory /* AbstractTileFactory */ {

	/**
	 * Constructor
	 * 
	 * @param tileProvider the tile provider
	 * @param cache the tile cache
	 */
	public CustomTileFactory(TileProvider tileProvider, TileCache cache) {
		super(tileProvider, cache);
	}

}
