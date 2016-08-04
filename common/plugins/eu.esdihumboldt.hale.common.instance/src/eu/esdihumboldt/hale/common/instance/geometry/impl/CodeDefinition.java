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

import javax.annotation.Nullable;

import org.geotools.gml2.SrsSyntax;
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
	private final boolean longitudeFirst;
	private CoordinateReferenceSystem crs;

	private static final LoadingCache<String, CoordinateReferenceSystem> CRS_CACHE = CacheBuilder
			.newBuilder().maximumSize(100).expireAfterAccess(1, TimeUnit.HOURS)
			.build(new CacheLoader<String, CoordinateReferenceSystem>() {

				@Override
				public CoordinateReferenceSystem load(String code) throws Exception {
					return CRS.decode(code);
				}

			});

	private static final LoadingCache<String, CoordinateReferenceSystem> LONGITUDE_FIRST_CRS_CACHE = CacheBuilder
			.newBuilder().maximumSize(100).expireAfterAccess(1, TimeUnit.HOURS)
			.build(new CacheLoader<String, CoordinateReferenceSystem>() {

				@Override
				public CoordinateReferenceSystem load(String code) throws Exception {
					return CRS.decode(code, true);
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
		this.longitudeFirst = false;
	}

	/**
	 * Create a code definition with only a code.
	 * 
	 * @param code the code
	 */
	public CodeDefinition(String code) {
		this(code, false);
	}

	/**
	 * Create a code definition with only a code.
	 * 
	 * @param code the code
	 * @param longitudeFirst if the axis order should be assumed as longitude
	 *            first
	 */
	public CodeDefinition(String code, boolean longitudeFirst) {
		this.code = code;
		this.crs = null;
		this.longitudeFirst = longitudeFirst;
	}

	/**
	 * @see CRSDefinition#getCRS()
	 */
	@Override
	public CoordinateReferenceSystem getCRS() {
		if (crs == null) {
			try {
				if (longitudeFirst) {
					crs = LONGITUDE_FIRST_CRS_CACHE.get(code);
				}
				else {
					crs = CRS_CACHE.get(code);
				}
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

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

	// helpers

	/**
	 * Extract EPSG code number from a given CRS code.
	 * 
	 * @param candidate the CRS code
	 * @return the EPSG code as string or <code>null</code> if it cannot be
	 *         identified
	 */
	@Nullable
	public static String extractEPSGCode(String candidate) {
		for (SrsSyntax srsSyntax : SrsSyntax.values()) {
			String code = extractCode(candidate, srsSyntax.getPrefix());
			if (code != null) {
				return code;
			}
		}

		// other syntax that may occur
		String prefix = "urn:ogc:def:crs:EPSG:";
		String code = extractCode(candidate, prefix);
		if (code != null) {
			return code;
		}

		return null;
	}

	/**
	 * Extract code number from a given CRS code for a specified prefix.
	 * 
	 * @param candidate the CRS code
	 * @param prefix the allowed prefix / authority
	 * @return the CRS code part w/o prefix or <code>null</code>
	 */
	@Nullable
	public static String extractCode(String candidate, String prefix) {
		if (candidate.length() > prefix.length()) {
			String authPart = candidate.substring(0, prefix.length());
			String codePart = candidate.substring(prefix.length());

			try {
				// ignore anything before the last colon
				int colonIndex = codePart.lastIndexOf(':');
				if (colonIndex >= 0) {
					codePart = codePart.substring(colonIndex + 1);
				}

				// check if codePart represents an integer
				Integer.parseInt(codePart);

				if (authPart.equalsIgnoreCase(prefix)) {
					return codePart;
				}
			} catch (NumberFormatException e) {
				// invalid
			}
		}
		return null;
	}

}
