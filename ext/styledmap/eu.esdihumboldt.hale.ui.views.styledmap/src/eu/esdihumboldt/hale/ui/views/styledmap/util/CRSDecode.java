/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.views.styledmap.util;

import java.util.HashMap;
import java.util.Map;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Utility class for CRS decoding.
 * 
 * @author Simon Templer
 */
public abstract class CRSDecode {

	/**
	 * Code mapped to CRS
	 */
	private static final Map<String, CoordinateReferenceSystem> crsMap = new HashMap<String, CoordinateReferenceSystem>();

	/**
	 * Get or create the CRS with the given code.
	 * 
	 * @param code the CRS code
	 * @return the coordinate reference system
	 * @throws NoSuchAuthorityCodeException if a code with an unknown authority
	 *             was supplied
	 * @throws FactoryException if creation of the CRS failed
	 */
	public synchronized static CoordinateReferenceSystem getCRS(String code)
			throws NoSuchAuthorityCodeException, FactoryException {
		CoordinateReferenceSystem crs = crsMap.get(code);

		if (crs == null) {
			crs = CRS.decode(code); // XXX
		}

		return crs;
	}

	/**
	 * Get or create the CRS with the given code.
	 * 
	 * @param epsg the EPSG code of the CRS
	 * @return the coordinate reference system
	 * @throws NoSuchAuthorityCodeException if EPSG is not known to the system
	 * @throws FactoryException if creation of the CRS failed
	 */
	public static CoordinateReferenceSystem getCRS(int epsg) throws NoSuchAuthorityCodeException,
			FactoryException {
		return getCRS("EPSG:" + epsg);
	}

}
