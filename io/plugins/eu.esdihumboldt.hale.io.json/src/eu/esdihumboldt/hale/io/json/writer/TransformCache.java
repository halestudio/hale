/*
 * Copyright (c) 2022 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.json.writer;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import eu.esdihumboldt.util.groovy.collector.GenericCollector;

/**
 * MathTransform to transform source CRS to target CRS.
 * 
 * @author Simon Templer
 */
public class TransformCache {

	private static class Cache
			extends GenericCollector<CoordinateReferenceSystem, MathTransform, Cache> {

		@Override
		protected Cache createCollector() {
			return new Cache();
		}

	}

	private final Cache cache = new Cache();

	/**
	 * MathTransform to transform source CRS to target CRS.
	 * 
	 * @param source CRS
	 * @param target CRS
	 * @return transformed data
	 * @throws Exception in case of unexpected cases.
	 */
	public MathTransform getTransform(CoordinateReferenceSystem source,
			CoordinateReferenceSystem target) throws Exception {
		Cache pair = cache.getAt(source).getAt(target);
		if (pair.value() == null) {
			pair.set(CRS.findMathTransform(source, target, false));
		}
		return pair.value();
	}

}