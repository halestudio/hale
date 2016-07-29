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

/**
 * AbstractPixelConverter
 *
 * @author Simon Templer
 */
public abstract class AbstractPixelConverter implements PixelConverter {

	/**
	 * The converter for {@link GeoPosition}s
	 */
	protected final GeoConverter geoConverter;

	/**
	 * Constructor
	 * 
	 * @param geoConverter the geo converter
	 */
	public AbstractPixelConverter(GeoConverter geoConverter) {
		this.geoConverter = geoConverter;
	}

}
