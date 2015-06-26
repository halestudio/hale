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

import java.util.concurrent.TimeUnit;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;

/**
 * CRS definition based on a code
 * 
 * @author Simon Templer
 */
public class CodeDefinition implements CRSDefinition {

	private static final long serialVersionUID = -7637649402983702957L;

	private final String code;
	private CoordinateReferenceSystem crs;

	private static final LoadingCache<String, CoordinateReferenceSystem> CRS_CACHE = CacheBuilder
			.newBuilder().maximumSize(100).expireAfterAccess(1, TimeUnit.HOURS)
			.build(new CacheLoader<String, CoordinateReferenceSystem>() {

				@Override
				public CoordinateReferenceSystem load(String code) throws Exception {
					return CRS.decode(code);
				}

			});

	/**
	 * Constructor
	 * 
	 * @param code the CRS code (e.g. EPSG:4326)
	 * @param crs the coordinate reference system, may be <code>null</code>
	 */
	public CodeDefinition(String code, CoordinateReferenceSystem crs) {
		this.code = code;
		this.crs = crs;
	}

	/**
	 * @see CRSDefinition#getCRS()
	 */
	@Override
	public CoordinateReferenceSystem getCRS() {
		if (crs == null) {
			try {
				crs = CRS_CACHE.get(code);
			} catch (Exception e) {
				throw new IllegalStateException("Invalid CRS code", e);
			}
		}

		return crs;
	}

	/**
	 * Get the CRS code
	 * 
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
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
		CodeDefinition other = (CodeDefinition) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		}
		else if (!code.equals(other.code))
			return false;
		return true;
	}

}
