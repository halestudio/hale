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

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.mapviewer.server.MapServer;
import de.fhg.igd.mapviewer.server.MapServerFactory;
import de.fhg.igd.mapviewer.server.MapServerFactoryCollection;

/**
 * Represents the {@link MapServer} extensions
 * 
 * @author Simon Templer
 */
public class MapServerExtension extends AbstractExtension<MapServer, MapServerFactory> {

	/**
	 * Default constructor
	 */
	public MapServerExtension() {
		super(MapServer.class.getName());
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected MapServerFactory createFactory(IConfigurationElement conf) throws Exception {

		if (conf.getName().equals("server")) { //$NON-NLS-1$
			return new ConfigurationMapServerFactory(conf);
		}

		return null;
	}

	/**
	 * @see AbstractExtension#createCollection(IConfigurationElement)
	 */
	@Override
	protected ExtensionObjectFactoryCollection<MapServer, MapServerFactory> createCollection(
			IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("collection")) { //$NON-NLS-1$
			return (MapServerFactoryCollection) conf.createExecutableExtension("class"); //$NON-NLS-1$
		}

		return null;
	}

}
