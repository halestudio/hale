/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package de.fhg.igd.mapviewer.server.tiles;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.eclipse.util.extension.AbstractObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.mapviewer.server.MapServer;
import de.fhg.igd.mapviewer.server.MapServerFactory;
import de.fhg.igd.mapviewer.server.MapServerFactoryCollection;
import de.fhg.igd.mapviewer.server.tiles.wizard.CustomTileServerConfigurationWizard;

/**
 * CustomTileMapServerFactory
 * 
 * @author Arun
 */
public class CustomTileMapServerFactory implements MapServerFactoryCollection {

	/**
	 * Map server factory for Custom Tiles server
	 * 
	 * @author Arun
	 */
	public class CustomTileFactory extends AbstractObjectFactory<MapServer>
			implements MapServerFactory {

		private final String name;

		/**
		 * Constructor
		 * 
		 * @param name Name of the map
		 */
		public CustomTileFactory(String name) {
			this.name = name;
		}

		/**
		 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory#createExtensionObject()
		 */
		@Override
		public MapServer createExtensionObject() throws Exception {
			CustomTileMapServer server = new CustomTileMapServer();
			if (server.load(name)) {
				return server;
			}
			else {
				throw new IllegalArgumentException("Loading configuration " + name + " failed");
			}
		}

		/**
		 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory#dispose(java.lang.Object)
		 */
		@Override
		public void dispose(MapServer instance) {
			instance.cleanup();
		}

		/**
		 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return getTypeName() + ":" + getDisplayName();
		}

		/**
		 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return name;
		}

		/**
		 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return CustomTileMapServer.class.getName();
		}

		/**
		 * @see ExtensionObjectFactory#allowConfigure()
		 */
		@Override
		public boolean allowConfigure() {
			return true;
		}

		/**
		 * @see ExtensionObjectFactory#configure()
		 */
		@Override
		public boolean configure() {
			try {
				CustomTileMapServer server = (CustomTileMapServer) createExtensionObject();
				return CustomTileMapServerFactory.this.configure(server);
			} catch (Exception e) {
				return false;
			}
		}
	}

	private static final Log log = LogFactory.getLog(CustomTileMapServerFactory.class);

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection#getName()
	 */
	@Override
	public String getName() {
		return "Custom Tile Maps";
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection#allowRemove()
	 */
	@Override
	public boolean allowRemove() {
		return true;
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection#allowAddNew()
	 */
	@Override
	public boolean allowAddNew() {
		return true;
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection#addNew()
	 */
	@Override
	public MapServerFactory addNew() {
		MapServer server = createNewServer();
		if (server != null) {
			return new CustomTileFactory(server.getName());
		}
		return null;
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection#remove(de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory)
	 */
	@Override
	public boolean remove(MapServerFactory factory) {
		return CustomTileMapServer.removeConfiguration(factory.getDisplayName());
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection#getFactories()
	 */
	@Override
	public List<MapServerFactory> getFactories() {
		List<MapServerFactory> results = new LinkedList<MapServerFactory>();

		// check if any Map Server is configured?
		if (CustomTileMapServer.getConfigurationNames().length > 0) {
			for (String name : CustomTileMapServer.getConfigurationNames()) {
				results.add(new CustomTileFactory(name));
			}
		}
		else {
			// no, then add default one.
			results.add(addDefault());
		}
		return results;
	}

	private MapServerFactory addDefault() {
		MapServer server = addDefaultServer();
		if (server != null) {
			return new CustomTileFactory(server.getName());
		}
		return null;
	}

	/**
	 * Creates a default Custom Tile map server from stamen tiles. url :
	 * http://tile.stamen.com/terrain/{z}/{x}/{y}.jpg
	 * 
	 * @return the map server or <code>null</code>
	 */
	private MapServer addDefaultServer() {
		CustomTileMapServer server = new CustomTileMapServer();

		server.setName("Stamen Terrain");
		server.setUrlPattern("http://tile.stamen.com/terrain/{z}/{x}/{y}.jpg");
		server.setZoomLevel(16);
		server.setAttributionText(
				"Map tiles by Stamen Design, under CC BY 3.0. Data by OpenStreetMap, under CC BY SA.");

		server.save();
		return server;
	}

	/**
	 * Creates a new Custom Tile map server
	 * 
	 * @return the map server or <code>null</code>
	 */
	private MapServer createNewServer() {
		CustomTileMapServer server = new CustomTileMapServer();

		if (configure(server)) {
			return server;
		}
		else {
			return null;
		}
	}

	/**
	 * Configure the given WMS map server
	 * 
	 * @param server the WMS map server
	 * @return if the configuration was saved
	 */
	private boolean configure(CustomTileMapServer server) {
		try {
			final Display display = PlatformUI.getWorkbench().getDisplay();

			CustomTileServerConfigurationWizard wizard = new CustomTileServerConfigurationWizard(
					server);
			WizardDialog dialog = new WizardDialog(display.getActiveShell(), wizard);
			if (dialog.open() == WizardDialog.OK) {
				server.save();
				return true;
			}
			else
				return false;
		} catch (Exception e) {
			log.error("Error configuring custom tile map server", e);
			return false;
		}
	}

}
