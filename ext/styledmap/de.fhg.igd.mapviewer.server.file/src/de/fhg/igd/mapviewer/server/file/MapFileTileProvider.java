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
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.mapviewer.AbstractTileProvider;
import org.jdesktop.swingx.mapviewer.BasicTileProvider;
import org.jdesktop.swingx.mapviewer.PixelConverter;
import org.jdesktop.swingx.mapviewer.TileProvider;

/**
 * TileProvider based on a map file created by {@link FileTiler}
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class MapFileTileProvider extends BasicTileProvider {

	private static final Log log = LogFactory.getLog(MapFileTileProvider.class);

	private final Properties converterProperties;

	private final File mapFile;

	private final int[] mapWidth;
	private final int[] mapHeight;

	/**
	 * Creates a {@link MapFileTileProvider}
	 * 
	 * @param mapFile the map file
	 * 
	 * @return the {@link TileProvider} for the given map file
	 * 
	 * @throws IOException if the map file cannot be read
	 * @throws MalformedURLException if an URI for a map file entry is invalid
	 */
	public static MapFileTileProvider createMapFileTileProvider(File mapFile)
			throws MalformedURLException, IOException {
		Properties mapProperties = new Properties();
		InputStreamReader mapReader = new InputStreamReader(
				getEntryURI(mapFile, FileTiler.MAP_PROPERTIES_FILE).toURL().openStream());
		try {
			mapProperties.load(mapReader);
		} finally {
			mapReader.close();
		}

		Properties converterProperties = new Properties();
		InputStreamReader converterReader = new InputStreamReader(
				getEntryURI(mapFile, FileTiler.CONVERTER_PROPERTIES_FILE).toURL().openStream());
		try {
			converterProperties.load(converterReader);
		} finally {
			converterReader.close();
		}

		// get informations from map properties
		int zoomLevels = Integer.parseInt(mapProperties.getProperty(FileTiler.PROP_ZOOM_LEVELS));

		int[] mapWidth = new int[zoomLevels];
		int[] mapHeight = new int[zoomLevels];

		for (int zoom = 0; zoom < zoomLevels; zoom++) {
			mapWidth[zoom] = Integer
					.parseInt(mapProperties.getProperty(FileTiler.PROP_MAP_WIDTH + zoom));
			mapHeight[zoom] = Integer
					.parseInt(mapProperties.getProperty(FileTiler.PROP_MAP_HEIGHT + zoom));
		}

		int tileWidth = Integer.parseInt(mapProperties.getProperty(FileTiler.PROP_TILE_WIDTH));
		int tileHeight = Integer.parseInt(mapProperties.getProperty(FileTiler.PROP_TILE_HEIGHT));

		return new MapFileTileProvider(mapFile, // map file
				converterProperties, // converter properties
				mapWidth, // map width array
				mapHeight, // map height array
				zoomLevels - 1, // default zoom
				0, // minimum zoom
				zoomLevels - 1, // maximum zoom
				zoomLevels - 1, // total map zoom
				tileWidth, // tile width
				tileHeight // tile height
		);
	}

	/**
	 * Creates an URI for an entry in the given jar file
	 * 
	 * @param jarFile the jar file
	 * @param entryName the entry name
	 * 
	 * @return the {@link URI} representing the entry
	 * @throws MalformedURLException if the given file provides no valid url
	 */
	public static URI getEntryURI(File jarFile, String entryName) throws MalformedURLException {
		return URI.create("jar:" + jarFile.toURI().toURL().toString() + "!/" + entryName); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Constructor
	 * 
	 * @param mapFile the map file
	 * @param converterProperties the converter properties
	 * @param mapWidth the map width
	 * @param mapHeight the map height
	 * @param defaultZoom the default zoom
	 * @param minimumZoom the minimum zoom
	 * @param maximumZoom the maximum zoom
	 * @param totalMapZoom the total map zoom
	 * @param tileWidth the tile width
	 * @param tileHeight the tile height
	 */
	public MapFileTileProvider(File mapFile, Properties converterProperties, int[] mapWidth,
			int[] mapHeight, int defaultZoom, int minimumZoom, int maximumZoom, int totalMapZoom,
			int tileWidth, int tileHeight) {
		super(defaultZoom, minimumZoom, maximumZoom, totalMapZoom, tileWidth, tileHeight);

		this.converterProperties = converterProperties;
		this.mapFile = mapFile;
		this.mapHeight = mapHeight;
		this.mapWidth = mapWidth;
	}

	/**
	 * @see AbstractTileProvider#createConverter()
	 */
	@Override
	protected PixelConverter createConverter() {
		return PropertiesConverterFactory.createConverter(converterProperties, this);
	}

	/**
	 * @see TileProvider#getMapHeightInTiles(int)
	 */
	@Override
	public int getMapHeightInTiles(int zoom) {
		if (zoom < mapHeight.length)
			return mapHeight[zoom];
		else
			return 1; // FIXME
	}

	/**
	 * @see TileProvider#getMapWidthInTiles(int)
	 */
	@Override
	public int getMapWidthInTiles(int zoom) {
		if (zoom < mapWidth.length)
			return mapWidth[zoom];
		else
			return 1; // FIXME
	}

	/**
	 * @see TileProvider#getTileUris(int, int, int)
	 */
	@Override
	public URI[] getTileUris(int x, int y, int zoom) {
		try {
			URI r = getEntryURI(mapFile,
					FileTiler.TILE_FILE_PREFIX + zoom + FileTiler.TILE_FILE_SEPARATOR
							+ String.valueOf(x + y * getMapWidthInTiles(zoom))
							+ FileTiler.TILE_FILE_EXTENSION);
			return new URI[] { r };
		} catch (MalformedURLException e) {
			log.error("Error creating tile uri", e); //$NON-NLS-1$
			return null;
		}
	}

}
