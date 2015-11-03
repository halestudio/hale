/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
			crs = CRS.decode(code);
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

	/**
	 * Code mapped to CRS
	 */
	private static final Map<String, CoordinateReferenceSystem> crsMapLonLat = new HashMap<String, CoordinateReferenceSystem>();

	/**
	 * Get or create the CRS with the given code.
	 * 
	 * @param code the CRS code
	 * @return the coordinate reference system
	 * @throws NoSuchAuthorityCodeException if a code with an unknown authority
	 *             was supplied
	 * @throws FactoryException if creation of the CRS failed
	 */
	public synchronized static CoordinateReferenceSystem getLonLatCRS(String code)
			throws NoSuchAuthorityCodeException, FactoryException {
		CoordinateReferenceSystem crs = crsMapLonLat.get(code);

		if (crs == null) {
			crs = CRS.decode(code, true);
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
	public static CoordinateReferenceSystem getLonLatCRS(int epsg)
			throws NoSuchAuthorityCodeException, FactoryException {
		return getLonLatCRS("EPSG:" + epsg);
	}

}
