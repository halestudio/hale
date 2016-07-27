/*
 * EmptyTileFactory.java
 *
 * Created on June 7, 2006, 4:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer.empty;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.jdesktop.swingx.mapviewer.GeoConverter;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;
import org.jdesktop.swingx.mapviewer.Tile;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.TileFactoryInfoTileProvider;
import org.jdesktop.swingx.mapviewer.TileProvider;

/**
 * A null implementation of TileFactory. Draws empty areas.
 * 
 * @author joshy
 */
@SuppressWarnings("deprecation")
public class EmptyTileFactory implements TileFactory {

	/** The empty tile image. */
	private BufferedImage emptyTile;

	private final TileProvider provider;

	/** Creates a new instance of EmptyTileFactory using the specified info. */
	public EmptyTileFactory() {
		provider = new TileFactoryInfoTileProvider(new TileFactoryInfo("EmptyTileFactory 256x256",
				1, 15, 17, 256, true, true, "x", "y", "z", ""), new GeoConverter() {

					@Override
					public GeoPosition convert(GeoPosition pos, int targetEpsg)
							throws IllegalGeoPositionException {
						if (pos.getEpsgCode() == targetEpsg) {
							return new GeoPosition(pos.getX(), pos.getY(), targetEpsg);
						}
						else
							throw new IllegalGeoPositionException();
					}
				});

		int tileWidth = provider.getTileWidth(provider.getMinimumZoom());
		int tileHeight = provider.getTileHeight(provider.getMinimumZoom());
		emptyTile = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = emptyTile.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, tileWidth, tileHeight);
		g.setColor(Color.WHITE);
		g.drawOval(10, 10, tileWidth - 20, tileHeight - 20);
		g.fillOval(70, 50, 20, 20);
		g.fillOval(tileWidth - 90, 50, 20, 20);
		g.fillOval(tileWidth / 2 - 10, tileHeight / 2 - 10, 20, 20);
		g.dispose();
	}

	/**
	 * Gets an instance of an empty tile for the given tile position and zoom on
	 * the world map.
	 * 
	 * @param x The tile's x position on the world map.
	 * @param y The tile's y position on the world map.
	 * @param zoom The current zoom level.
	 */
	@Override
	public Tile getTile(int x, int y, int zoom) {
		return new Tile(x, y, zoom) {

			@Override
			public boolean isLoaded() {
				return true;
			}

			@Override
			public BufferedImage getImage() {
				return emptyTile;
			}

		};
	}

	/**
	 * Override this method to load the tile using, for example, an
	 * <code>ExecutorService</code>.
	 * 
	 * @param tile The tile to load.
	 */
	@Override
	public void startLoading(Tile tile) {
		// noop
	}

	/**
	 * @see TileFactory#getTileProvider()
	 */
	@Override
	public TileProvider getTileProvider() {
		return provider;
	}

	/**
	 * @see TileFactory#cleanup()
	 */
	@Override
	public void cleanup() {
		// do nothing
	}

	/**
	 * @see TileFactory#clearCache()
	 */
	@Override
	public void clearCache() {
		// do nothing
	}

}
