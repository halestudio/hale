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

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Provides informations on tile constraints
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface TileConstraints {
	
	/**
	 * Get the tile width
	 * 
	 * @return the tile width in pixels
	 */
	public int getTileWidth();
	
	/**
	 * Get the tile height
	 * 
	 * @return the tile height in pixels
	 */
	public int getTileHeight();
	
	/**
	 * Get the tile area
	 * 
	 * @param zoom the tile zoom level
	 * @param x the tile x ordinate
	 * @param y the tile y ordinate
	 * @return the tile area (may be null)
	 */
	public ReferencedEnvelope getTileArea(int zoom, int x, int y);
	
	public CoordinateReferenceSystem getCRS();

}
