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
