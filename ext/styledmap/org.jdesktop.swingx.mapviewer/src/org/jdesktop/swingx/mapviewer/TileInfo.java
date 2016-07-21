package org.jdesktop.swingx.mapviewer;

import java.net.URI;

/**
 * TileInfo
 * 
 * @author Simon Templer
 */
public interface TileInfo {

	/**
	 * @return the zoom level that this tile belongs in
	 */
	public abstract int getZoom();

	/**
	 * Gets the URI of this tile.
	 * 
	 * @return the tile image URI
	 */
	public abstract URI getURI();

	/**
	 * Gets a URI that uniquely identifies this tile (does not have to be the
	 * same URI as the one returned by {@link #getURI()})
	 * 
	 * @return the unique URI
	 */
	public abstract URI getIdentifier();

	/**
	 * Get the tile x ordinate
	 * 
	 * @return the tile x ordinate
	 */
	public abstract int getX();

	/**
	 * Get the tile y ordinate
	 * 
	 * @return the tile y ordinate
	 */
	public abstract int getY();

}