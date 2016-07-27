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
package de.fhg.igd.mapviewer.server.file;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jdesktop.swingx.mapviewer.TileProvider;

import de.fhg.igd.eclipse.util.extension.AbstractObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.mapviewer.server.MapServer;
import de.fhg.igd.mapviewer.server.MapServerFactory;
import de.fhg.igd.mapviewer.server.MapServerFactoryCollection;
import de.fhg.igd.mapviewer.server.TileProviderMapServer;

/**
 * MapFileServerFactory
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class MapFileServerFactory implements MapServerFactoryCollection {

	/**
	 * Map file server factory
	 */
	public class MapFileServer extends AbstractObjectFactory<MapServer>implements MapServerFactory {

		private final String name;

		/**
		 * Constructor
		 * 
		 * @param name the map name
		 */
		public MapFileServer(String name) {
			super();

			this.name = name;
		}

		/**
		 * @see ExtensionObjectFactory#createExtensionObject()
		 */
		@Override
		public MapServer createExtensionObject() throws Exception {
			return loadServer(name);
		}

		/**
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return name;
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return getTypeName() + ":" + getDisplayName(); //$NON-NLS-1$
		}

		/**
		 * @see ExtensionObjectDefinition#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return MapFileServerFactory.class.getName();
		}

		/**
		 * @see ExtensionObjectFactory#dispose(Object)
		 */
		@Override
		public void dispose(MapServer instance) {
			instance.cleanup();
		}

	}

	private static final Log log = LogFactory.getLog(MapFileServerFactory.class);

	private static final String NODE_MAP_FILES = "mapFiles"; //$NON-NLS-1$

	private final Map<MapServer, String> prefServers = new HashMap<MapServer, String>();

	private final Preferences mapFiles = Preferences.userNodeForPackage(MapFileServerFactory.class)
			.node(MapFileServerFactory.class.getSimpleName()).node(NODE_MAP_FILES);

	private final JFileChooser fileChooser = new JFileChooser();

	/**
	 * Default constructor
	 */
	public MapFileServerFactory() {
		fileChooser.setFileFilter(
				new FileNameExtensionFilter(Messages.getString("MapFileServerFactory.1"), //$NON-NLS-1$
						FileTiler.MAP_ARCHIVE_EXTENSION.substring(1)));
	}

	/**
	 * @see ExtensionObjectFactoryCollection#getFactories()
	 */
	@Override
	public List<MapServerFactory> getFactories() {
		List<MapServerFactory> results = new LinkedList<MapServerFactory>();

		// load stored map files
		try {
			for (String name : mapFiles.keys()) {
				results.add(new MapFileServer(name));
			}
		} catch (BackingStoreException e) {
			log.error("Error loading preferences", e);
		}

		return results;
	}

	/**
	 * Load a map file server with the given name
	 * 
	 * @param name the server name
	 * @return the map server or null
	 */
	private MapServer loadServer(String name) {
		String filename = mapFiles.get(name, null);
		if (filename == null) {
			mapFiles.remove(name);
		}
		else {
			File file = new File(filename);
			if (file.exists()) {
				TileProvider tp;
				try {
					tp = MapFileTileProvider.createMapFileTileProvider(file);
					MapServer server = new TileProviderMapServer(tp);
					server.setName(name);
					prefServers.put(server, name);
					return server;
				} catch (MalformedURLException e) {
					log.error("Invalid file name", e);
				} catch (IOException e) {
					log.error("Error loading map file", e);
				}
			}
			else {
				log.info("Map file not found, removing map: " + filename);
			}
		}

		return null;
	}

	/**
	 * Create a new server and add it to the collection
	 * 
	 * @return the new server or <code>null</code>
	 */
	private MapServer createNewServer() {
		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);

		dialog.setFilterNames(new String[] { Messages.getString("MapFileServerFactory.6") }); //$NON-NLS-1$
		dialog.setFilterExtensions(new String[] { "*.map" }); //$NON-NLS-1$

		String openName = dialog.open();
		if (openName != null) {
			File file = new File(openName);

			// XXX if (fileChooser.showOpenDialog(null) ==
			// JFileChooser.APPROVE_OPTION) {
			// XXX File file = fileChooser.getSelectedFile();

			if (file.exists()) {
				String name = file.getName();

				int i = 1;
				while (mapFiles.get(name, null) != null) {
					name = file.getName() + "_" + i; //$NON-NLS-1$
					i++;
				}

				mapFiles.put(name, file.getAbsolutePath());

				return loadServer(name);
			}
		}

		return null;
	}

	/**
	 * @see ExtensionObjectFactoryCollection#addNew()
	 */
	@Override
	public MapServerFactory addNew() {
		MapServer server = createNewServer();
		if (server != null) {
			return new MapFileServer(server.getName());
		}
		else {
			return null;
		}
	}

	/**
	 * @see ExtensionObjectFactoryCollection#allowAddNew()
	 */
	@Override
	public boolean allowAddNew() {
		return true;
	}

	/**
	 * @see ExtensionObjectFactoryCollection#allowRemove()
	 */
	@Override
	public boolean allowRemove() {
		return true;
	}

	/**
	 * @see ExtensionObjectFactoryCollection#remove(ExtensionObjectFactory)
	 */
	@Override
	public boolean remove(MapServerFactory factory) {
		removeServer(factory.getDisplayName());
		return true;
	}

	/**
	 * Remove the server with the given name
	 * 
	 * @param name the name
	 */
	private void removeServer(String name) {
		if (name != null) {
			mapFiles.remove(name);
		}
	}

	/**
	 * @see ExtensionObjectFactoryCollection#getName()
	 */
	@Override
	public String getName() {
		return Messages.getString("MapFileServerFactory.8"); //$NON-NLS-1$
	}

}
