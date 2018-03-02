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

package eu.esdihumboldt.hale.common.instance.geometry.impl;

import java.util.HashMap;
import java.util.Map;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.esdihumboldt.hale.common.instance.geometry.CRSDefinitionUtil;
import eu.esdihumboldt.hale.common.instance.geometry.CRSResolveCache;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;

/**
 * Tries to resolve a CRS against the EPSG database.
 * 
 * @author Simon Templer
 */
public class EPSGResolveCache implements CRSResolveCache {

	private final Map<CoordinateReferenceSystem, CRSDefinition> cached = new HashMap<>();

	@Override
	public CRSDefinition resolveCRS(final CoordinateReferenceSystem crs) {
		CRSDefinition result = cached.get(crs);
		if (result == null) {
			result = CRSDefinitionUtil.lookupCrs(crs);
			cached.put(crs, result);
		}

		return result;
	}

	@Override
	public void reviseCache(CoordinateReferenceSystem crs, CRSDefinition crsDef) {
		if (crs == null) {
			return;
		}

		if (crsDef == null) {
			cached.remove(crs);
		}
		else {
			cached.put(crs, crsDef);
		}
	}

}
