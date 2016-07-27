/*+-------------+----------------------------------------------------------*
 *|  |  |_|_|_|_|   Fraunhofer-Institut fuer Graphische Datenverarbeitung  *
 *|__|__|_|_|_|_|     (Fraunhofer Institute for Computer Graphics)         *
 *|  |  |_|_|_|_|                                                          *
 *|__|__|_|_|_|_|                                                          *
 *|  __ |    ___|                                                          *
 *| /_  /_  / _ |     Fraunhoferstrasse 5                                  *
 *|/   / / /__/ |     D-64283 Darmstadt, Germany                           *
 *+-------------+----------------------------------------------------------*/
package org.jdesktop.swingx.mapviewer;

import org.jdesktop.swingx.painter.Painter;

/**
 * AbstractTileProvider
 *
 * @author Simon Templer
 */
public abstract class AbstractTileProvider implements TileProvider {

	private PixelConverter converter;

	private boolean allowHorizontalWrapping = false;
	private boolean drawTileBorders = false;

	/**
	 * Create the pixel converter
	 * 
	 * @return the pixel converter
	 */
	protected abstract PixelConverter createConverter();

	/**
	 * @see TileProvider#getConverter()
	 */
	@Override
	public PixelConverter getConverter() {
		if (converter == null)
			converter = createConverter();

		return converter;
	}

	/**
	 * @see TileProvider#getAllowHorizontalWrapping()
	 */
	@Override
	public boolean getAllowHorizontalWrapping() {
		return allowHorizontalWrapping;
	}

	/**
	 * @see TileProvider#getDrawTileBorders()
	 */
	@Override
	public boolean getDrawTileBorders() {
		return drawTileBorders;
	}

	/**
	 * Set if tile borders shall be drawn for this map
	 * 
	 * @param drawTileBorders if tile borders shall be drawn
	 */
	public void setDrawTileBorders(boolean drawTileBorders) {
		this.drawTileBorders = drawTileBorders;
	}

	/**
	 * Set if horizontal wrapping shall be allowed for this map
	 * 
	 * @param allowHorizontalWrapping if horizontal mapping shall be allowed
	 */
	public void setAllowHorizontalWrapping(boolean allowHorizontalWrapping) {
		this.allowHorizontalWrapping = allowHorizontalWrapping;
	}

	/**
	 * @see TileProvider#getMapOverlayPainter()
	 */
	@Override
	public Painter<JXMapViewer> getMapOverlayPainter() {
		return null;
	}

}
