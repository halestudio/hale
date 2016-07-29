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

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import de.fhg.igd.mapviewer.view.cache.ITileCacheService;
import de.fhg.igd.mapviewer.view.cache.TileCacheService;
import de.fhg.igd.mapviewer.view.overlay.IMapPainterService;
import de.fhg.igd.mapviewer.view.overlay.ITileOverlayService;
import de.fhg.igd.mapviewer.view.overlay.MapPainterService;
import de.fhg.igd.mapviewer.view.overlay.TileOverlayService;
import de.fhg.igd.mapviewer.view.server.IMapServerService;
import de.fhg.igd.mapviewer.view.server.MapServerService;

/**
 * Factory for map related services
 * 
 * @author Simon Templer
 */
public class MapServiceFactory extends AbstractServiceFactory {

	/**
	 * @see AbstractServiceFactory#create(Class, IServiceLocator,
	 *      IServiceLocator)
	 */
	@Override
	public Object create(Class serviceInterface, IServiceLocator parentLocator,
			IServiceLocator locator) {

		if (serviceInterface.equals(IMapPainterService.class)) {
			return new MapPainterService();
		}

		if (serviceInterface.equals(ITileOverlayService.class)) {
			return new TileOverlayService();
		}

		if (serviceInterface.equals(IMapServerService.class)) {
			return new MapServerService();
		}

		if (serviceInterface.equals(ITileCacheService.class)) {
			return new TileCacheService();
		}

		return null;
	}

}
