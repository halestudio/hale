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

package eu.esdihumboldt.hale.common.instance.geometry;

import org.geotools.referencing.CRS;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.geometry.impl.WKTDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;

/**
 * Utility methods related to {@link CRSDefinition}s
 * 
 * @author Simon Templer
 */
public abstract class CRSDefinitionUtil {

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

}
