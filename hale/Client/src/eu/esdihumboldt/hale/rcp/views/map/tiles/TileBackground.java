/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.map.tiles;

import org.eclipse.swt.graphics.GC;

/**
 * Draws tile backgrounds
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface TileBackground {
	
	/**
	 * Draw a tile background
	 * 
	 * @param gc the graphics device
	 * @param x the x position of the tile
	 * @param y the y position of the tile
	 * @param tileWidth the tile width
	 * @param tileHeight the tile height
	 */
	public void drawTileBackground(GC gc, int x, int y, int tileWidth, int tileHeight);

}
