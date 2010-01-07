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

import org.eclipse.swt.graphics.ImageData;

/**
 * Provides tiles as {@link ImageData}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface TileProvider {
	
	/**
	 * Get a tile
	 * 
	 * @param constraints the tile constraints
	 * @param zoom the tile zoom level
	 * @param x the tile x ordinate
	 * @param y the tile y ordinate
	 * @return the tile image data (may be null)
	 * @throws Exception if an error occurs while creating the tile
	 */
	public ImageData getTile(TileConstraints constraints, int zoom, int x, int y)
		throws Exception;

}
