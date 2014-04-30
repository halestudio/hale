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

import org.geotools.referencing.CRS;
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
	 * @return the CRS definition
	 */
	public static CRSDefinition createDefinition(CoordinateReferenceSystem crs) {
		ReferenceIdentifier name = crs.getName();
		// try by code
		if (name != null) {
			String code = name.getCode();
			if (code != null && !code.isEmpty()) {
				// try decoding
				try {
					crs = CRS.decode(code);
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
							code = "EPSG:" + epsgcode;
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
