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

package eu.esdihumboldt.hale.common.instance.geometry;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.geotools.gml2.SrsSyntax;
import org.geotools.referencing.CRS;
import org.geotools.referencing.CRS.AxisOrder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.geometry.impl.WKTDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;

/**
 * Utility methods related to {@link CRSDefinition}s
 * 
 * @author Simon Templer
 */
public abstract class CRSDefinitionUtil {

	private static final ALogger log = ALoggerFactory.getLogger(CRSDefinitionUtil.class);

	private static final String NO_CODE = "";
	private static final Map<CRSDefinition, String> crsCodes = new HashMap<>();

	/**
	 * Create a {@link CRSDefinition} from an existing coordinate reference
	 * system.
	 * 
	 * @param crs the coordinate reference system
	 * @param cache the cache for CRS resolving
	 * @return the CRS definition
	 */
	public static CRSDefinition createDefinition(CoordinateReferenceSystem crs,
			@Nullable CRSResolveCache cache) {
		ReferenceIdentifier name = crs.getName();
		// try to find CRS in EPSG DB
		CRSDefinition def;
		if (cache != null) {
			def = cache.resolveCRS(crs);
		}
		else {
			def = lookupCrs(crs);
		}

		if (def != null) {
			return def;
		}

		// try by code
		if (name != null) {
			String code = name.getCode();
			if (code != null && !code.isEmpty()) {
				// try decoding
				try {
					boolean lonFirst = (CRS.getAxisOrder(crs) == AxisOrder.EAST_NORTH);
					crs = CRS.decode(code, lonFirst);
					return new CodeDefinition(code, crs);
				} catch (Exception e) {
					// ignore
				}
			}
		}

		// use WKT
		return new WKTDefinition(crs.toWKT(), crs);
	}

	/**
	 * Try to lookup the given CRS via Geotools. If the CRS can be resolved, the
	 * returned {@link CRSDefinition} will contain a
	 * {@link CoordinateReferenceSystem} with additional information like
	 * Bursa-Wolf parameters, otherwise the WKT definition of the input CRS will
	 * be used as is.
	 * 
	 * @param crs The CRS to look up
	 * @return A {@link CodeDefinition} with the resolved CRS or a
	 *         {@link WKTDefinition} if the CRS could not be resolved.
	 */
	public static CRSDefinition lookupCrs(CoordinateReferenceSystem crs) {
		try {
			Integer epsgCode = CRS.lookupEpsgCode(crs, true);
			if (epsgCode != null) {
				// We must use the "EPSG:" prefix here, otherwise Geotools will
				// not honour the longitudeFirst parameter and will always
				// return the lat/lon variant...
				String code = SrsSyntax.EPSG_CODE.getPrefix() + String.valueOf(epsgCode);

				// Look up the code
				CoordinateReferenceSystem resolved = CRS.decode(code);

				// Use the CRS resolved by GeoTools only if its axis order
				// is the same as the axis order of the provided CRS (not
				// guaranteed)
				if (CRS.getAxisOrder(crs).equals(CRS.getAxisOrder(resolved))) {
					return new CodeDefinition(code, resolved);
				}
			}
		} catch (FactoryException e) {
			// Ignore
		}

		return new WKTDefinition(crs.toWKT(), crs);
	}

	/**
	 * Get the code for a CRS definition if possible.
	 * 
	 * @param def the CRS definition
	 * @return the CRS code representing the definition or <code>null</code>
	 */
	public static String getCode(CRSDefinition def) {
		if (def instanceof CodeDefinition) {
			return ((CodeDefinition) def).getCode();
		}
		else {
			synchronized (crsCodes) {
				String code = crsCodes.get(def);
				if (code == null) {
					// try to look up code
					try {
						Integer epsgcode = CRS.lookupEpsgCode(def.getCRS(), false);
						if (epsgcode == null) {
							// full scan
							CRS.lookupEpsgCode(def.getCRS(), true);
						}
						if (epsgcode != null) {
							code = SrsSyntax.OGC_URN.getPrefix() + epsgcode;
							// TODO support formatting the code?
						}
					} catch (Exception e) {
						log.error("Error while trying to look up EPSG code for CRS", e);
					}

					if (code != null) {
						crsCodes.put(def, code);
					}
					else {
						crsCodes.put(def, NO_CODE);
					}
					return code;
				}
				else {
					if (NO_CODE.equals(code))
						return null;
					else
						return code;
				}
			}
		}
	}

	/**
	 * Get the EPSG number for a CRS definition if possible.
	 * 
	 * @param def the CRS definition
	 * @return the EPSG code representing the definition or <code>null</code>
	 */
	public static String getEPSG(CRSDefinition def) {
		String code = getCode(def);
		if (code == null)
			return null;
		if (!code.contains("EPSG"))
			return null;
		return code.substring(code.lastIndexOf(':') + 1);
	}

}
