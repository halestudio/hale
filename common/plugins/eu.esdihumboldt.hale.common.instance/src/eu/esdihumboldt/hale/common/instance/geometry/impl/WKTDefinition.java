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

package eu.esdihumboldt.hale.common.instance.geometry.impl;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;

/**
 * CRS definition based on WKT
 * 
 * @author Simon Templer
 */
public class WKTDefinition implements CRSDefinition {

	private static final long serialVersionUID = -201452960771910038L;

	private final String wkt;
	private CoordinateReferenceSystem crs;

	/**
	 * Constructor
	 * 
	 * @param wkt the WKT defining the CRS
	 * @param crs the coordinate reference system, may be <code>null</code>
	 */
	public WKTDefinition(String wkt, CoordinateReferenceSystem crs) {
		this.wkt = wkt;
		this.crs = crs;
	}

	/**
	 * @see CRSDefinition#getCRS()
	 */
	@Override
	public CoordinateReferenceSystem getCRS() {
		if (crs == null) {
			try {
				crs = CRS.parseWKT(wkt);
			} catch (Exception e) {
				throw new IllegalStateException("Invalid WKT for defining a CRS", e);
			}
		}

		return crs;
	}

	/**
	 * Get the WKT
	 * 
	 * @return the well known text representation of the CRS
	 */
	public String getWkt() {
		return wkt;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((wkt == null) ? 0 : wkt.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WKTDefinition other = (WKTDefinition) obj;
		if (wkt == null) {
			if (other.wkt != null)
				return false;
		}
		else if (!wkt.equals(other.wkt))
			return false;
		return true;
	}

}
