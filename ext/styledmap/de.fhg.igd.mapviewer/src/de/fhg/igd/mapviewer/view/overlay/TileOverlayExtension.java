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
package de.fhg.igd.mapviewer.view.overlay;

import org.eclipse.core.runtime.IConfigurationElement;
import org.jdesktop.swingx.mapviewer.TileOverlayPainter;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.mapviewer.AbstractTileOverlayPainter;

/**
 * Tile overlay extension.
 * 
 * @author Simon Templer
 */
public class TileOverlayExtension
		extends AbstractExtension<TileOverlayPainter, TileOverlayFactory> {

	/**
	 * {@link TileOverlayPainter} factory based on an
	 * {@link IConfigurationElement}
	 */
	public static class ConfigurationTileOverlayFactory
			extends AbstractConfigurationFactory<TileOverlayPainter>implements TileOverlayFactory {

		/**
		 * Constructor
		 * 
		 * @param conf the configuration element
		 */
		protected ConfigurationTileOverlayFactory(IConfigurationElement conf) {
			super(conf, "class"); //$NON-NLS-1$
		}

		/**
		 * @see AbstractConfigurationFactory#createExtensionObject()
		 */
		@Override
		public TileOverlayPainter createExtensionObject() throws Exception {
			TileOverlayPainter result = super.createExtensionObject();

			try {
				int priority = Integer.parseInt(conf.getAttribute("priority")); //$NON-NLS-1$

				if (result instanceof AbstractTileOverlayPainter) {
					((AbstractTileOverlayPainter) result).setPriority(priority);
				}
			} catch (Exception e) {
				// ignore
			}

			return result;
		}

		/**
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return conf.getAttribute("name"); //$NON-NLS-1$
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return conf.getAttribute("id"); //$NON-NLS-1$
		}

		/**
		 * @see ExtensionObjectFactory#dispose(Object)
		 */
		@Override
		public void dispose(TileOverlayPainter instance) {
			instance.dispose();
		}

		/**
		 * @see TileOverlayFactory#showInMiniMap()
		 */
		@Override
		public boolean showInMiniMap() {
			return Boolean.parseBoolean(conf.getAttribute("showInMiniMap")); //$NON-NLS-1$
		}

	}

	/**
	 * Default constructor
	 */
	public TileOverlayExtension() {
		super("de.fhg.igd.mapviewer.TileOverlayPainter"); //$NON-NLS-1$
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected TileOverlayFactory createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("painter")) { //$NON-NLS-1$
			return new ConfigurationTileOverlayFactory(conf);
		}

		return null;
	}

	/**
	 * @see AbstractExtension#createCollection(org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected ExtensionObjectFactoryCollection<TileOverlayPainter, TileOverlayFactory> createCollection(
			IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("collection")) { //$NON-NLS-1$
			TileOverlayFactoryCollection result = (TileOverlayFactoryCollection) conf
					.createExecutableExtension("class"); //$NON-NLS-1$

			try {
				int priority = Integer.parseInt(conf.getAttribute("priority")); //$NON-NLS-1$

				result.setPriority(priority);
			} catch (Exception e) {
				// ignore
			}

			return result;
		}

		return null;
	}

}
