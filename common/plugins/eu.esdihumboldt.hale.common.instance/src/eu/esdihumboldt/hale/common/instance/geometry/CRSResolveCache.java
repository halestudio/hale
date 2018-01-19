/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.geometry;

import javax.annotation.Nullable;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;

/**
 * Resolves a CRS to a CRS definition that can't be directly derived from the
 * CRS.
 * 
 * @author Simon Templer
 */
public interface CRSResolveCache {

	/**
	 * Resolve a CRS to a CRS definition that can't be directly derived from the
	 * CRS.
	 * 
	 * @param crs the CRS
	 * @return the CRS definition or <code>null</code>
	 */
	@Nullable
	CRSDefinition resolveCRS(CoordinateReferenceSystem crs);

	/**
	 * Corrects the cached {@link CRSDefinition} for the given
	 * {@link CoordinateReferenceSystem}
	 * 
	 * @param crs the CRS
	 * @param crsDef the CRS definition or <code>null</code> to remove the
	 *            cached definition
	 */
	void reviseCache(CoordinateReferenceSystem crs, CRSDefinition crsDef);
}
