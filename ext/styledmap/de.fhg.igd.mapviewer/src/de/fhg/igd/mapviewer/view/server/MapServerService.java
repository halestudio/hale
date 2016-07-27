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
package de.fhg.igd.mapviewer.view.server;

import java.util.List;

import de.fhg.igd.eclipse.ui.util.extension.exclusive.PreferencesExclusiveExtension;
import de.fhg.igd.eclipse.util.extension.AbstractObjectFactory;
import de.fhg.igd.mapviewer.server.EmptyMapServer;
import de.fhg.igd.mapviewer.server.MapServer;
import de.fhg.igd.mapviewer.server.MapServerFactory;
import de.fhg.igd.mapviewer.view.MapviewerPlugin;
import de.fhg.igd.mapviewer.view.preferences.MapPreferenceConstants;

/**
 * Service managing the current {@link MapServer}
 * 
 * @author Simon Templer
 */
public class MapServerService extends PreferencesExclusiveExtension<MapServer, MapServerFactory>
		implements IMapServerService {

	/**
	 * Empty map server factory
	 */
	public static class EmptyMapFactory extends AbstractObjectFactory<MapServer>
			implements MapServerFactory {

		@Override
		public String getTypeName() {
			return EmptyMapServer.class.getName();
		}

		@Override
		public String getIdentifier() {
			return getTypeName() + ":" + getDisplayName(); //$NON-NLS-1$
		}

		@Override
		public String getDisplayName() {
			return "Dummy"; //$NON-NLS-1$
		}

		@Override
		public MapServer createExtensionObject() throws Exception {
			MapServer server = new EmptyMapServer();
			server.setName(getDisplayName());
			return server;
		}

		@Override
		public void dispose(MapServer instance) {
			instance.cleanup();
		}

	}

	/**
	 * Default constructor
	 */
	public MapServerService() {
		super(new MapServerExtension(), MapviewerPlugin.getDefault().getPreferenceStore(),
				MapPreferenceConstants.CURRENT_MAP_SERVER);
	}

	/**
	 * @see PreferencesExclusiveExtension#getFallbackFactory()
	 */
	@Override
	protected MapServerFactory getFallbackFactory() {
		return new EmptyMapFactory();
	}

	/**
	 * @see PreferencesExclusiveExtension#getDefaultFactory(List)
	 */
	@Override
	protected MapServerFactory getDefaultFactory(List<MapServerFactory> factories) {
		// return the factory with the highest priority
		MapServerFactory result = null;

		for (MapServerFactory factory : factories) {
			if (result == null) {
				result = factory;
			}
			else {
				if (factory.getPriority() < result.getPriority()) {
					result = factory;
				}
			}
		}

		return result;
	}

}
