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

package de.fhg.igd.mapviewer.view.cache;

import org.eclipse.core.runtime.IConfigurationElement;
import org.jdesktop.swingx.mapviewer.TileCache;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.AbstractObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;

/**
 * {@link TileCache} extension
 * 
 * @author Simon Templer
 */
public class TileCacheExtension extends AbstractExtension<TileCache, ITileCacheFactory> {

	/**
	 * Default {@link TileCache} factory for configuration elements
	 */
	public class ConfigurationFactory extends AbstractConfigurationFactory<TileCache>
			implements ITileCacheFactory {

		/**
		 * Constructor
		 * 
		 * @param conf the configuration element
		 */
		public ConfigurationFactory(IConfigurationElement conf) {
			super(conf, "class"); //$NON-NLS-1$
		}

		/**
		 * @see ExtensionObjectFactory#dispose(Object)
		 */
		@Override
		public void dispose(TileCache instance) {
			instance.clear();
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return conf.getAttribute("id"); //$NON-NLS-1$
		}

		/**
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return conf.getAttribute("name"); //$NON-NLS-1$
		}

		/**
		 * @see AbstractObjectDefinition#getPriority()
		 */
		@Override
		public int getPriority() {
			String order = conf.getAttribute("order"); //$NON-NLS-1$
			if (order != null && !order.isEmpty()) {
				return Integer.parseInt(order);
			}
			else {
				return 0;
			}
		}

	}

	/**
	 * The extension ID
	 */
	public static final String ID = "de.fhg.igd.mapviewer.TileCache"; //$NON-NLS-1$

	/**
	 * Default constructor
	 */
	public TileCacheExtension() {
		super(ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected ITileCacheFactory createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("cache")) { //$NON-NLS-1$
			return new ConfigurationFactory(conf);
		}

		return null;
	}

}
