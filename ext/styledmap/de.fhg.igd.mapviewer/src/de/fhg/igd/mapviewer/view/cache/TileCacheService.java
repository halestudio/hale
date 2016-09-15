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

import org.jdesktop.swingx.mapviewer.DefaultTileCache;
import org.jdesktop.swingx.mapviewer.TileCache;

import de.fhg.igd.eclipse.ui.util.extension.exclusive.PreferencesExclusiveExtension;
import de.fhg.igd.eclipse.util.extension.AbstractObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.mapviewer.view.MapviewerPlugin;
import de.fhg.igd.mapviewer.view.preferences.MapPreferenceConstants;

/**
 * *{@link TileCache} extension service
 * 
 * @author Simon Templer
 */
public class TileCacheService extends PreferencesExclusiveExtension<TileCache, ITileCacheFactory>
		implements ITileCacheService {

	/**
	 * {@link DefaultTileCache} factory for fallback
	 */
	public class DefaultFactory extends AbstractObjectFactory<TileCache>
			implements ITileCacheFactory {

		/**
		 * @see ExtensionObjectFactory#createExtensionObject()
		 */
		@Override
		public TileCache createExtensionObject() throws Exception {
			return new DefaultTileCache();
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
			return "de.fhg.igd.mapviewer.cache.default"; //$NON-NLS-1$
		}

		/**
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return "Default"; //$NON-NLS-1$
		}

		/**
		 * @see ExtensionObjectDefinition#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return DefaultTileCache.class.getName();
		}

	}

	/**
	 * Default constructor
	 */
	public TileCacheService() {
		super(new TileCacheExtension(), MapviewerPlugin.getDefault().getPreferenceStore(),
				MapPreferenceConstants.CACHE);
	}

	/**
	 * @see PreferencesExclusiveExtension#getFallbackFactory()
	 */
	@Override
	protected ITileCacheFactory getFallbackFactory() {
		return new DefaultFactory();
	}

}
