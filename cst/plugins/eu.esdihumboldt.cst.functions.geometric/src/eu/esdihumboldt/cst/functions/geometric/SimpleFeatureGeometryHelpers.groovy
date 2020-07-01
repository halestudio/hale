/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.cst.functions.geometric

import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryCollection
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.MultiLineString

import eu.esdihumboldt.cst.functions.groovy.helper.spec.SpecBuilder
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import eu.esdihumboldt.util.geometry.CurveHelper
import groovy.transform.CompileStatic

/**
 * Helpers related to simple feature geometries.
 * 
 * @author Simon Templer
 */
class SimpleFeatureGeometryHelpers {

	private static final GeometryFactory fact = new GeometryFactory()

	/**
	 * Specification for the splitMulti function
	 */
	public static final eu.esdihumboldt.cst.functions.groovy.helper.spec.Specification _toSimpleGeometries_spec = SpecBuilder.newSpec( //
	description: 'Convert source geometries to SimpleFeature geometries.',
	result: 'the list of simple geometries (each wrapped in a GeometryProperty)') {
		//
		geometries('A single or multiple (as a list/iterable) geometries, geometry properties or instances holding a geometry')
		collections('if geometry collections like MultiLineString and MultiPolygon are allowed as result', defaultValue: true)
	}

	@CompileStatic
	static Collection<GeometryProperty<? extends Geometry>> _toSimpleGeometries(Map args) {
		boolean collections = args.collections != null ? args.collections as boolean : true

		List<GeometryProperty<? extends Geometry>> geoms = GeometryHelperFunctions._findAll(args.geometries)

		geoms.collectMany { GeometryProperty<? extends Geometry> geom ->
			SimpleFeatureGeometryHelpers.toSimpleGeometries(geom, collections)
		}
	}

	static Collection<GeometryProperty<? extends Geometry>> toSimpleGeometries(
			GeometryProperty<? extends Geometry> geometry, boolean collections) {

		Geometry geom = geometry.getGeometry()

		if (geom instanceof MultiLineString) {
			// always try to aggregate MultiLineString (in case it is a CompoundCurve)
			LineString singleCurve = CurveHelper.combineCurve(geom, fact)
			if (singleCurve) {
				return [
					new DefaultGeometryProperty(geometry.CRSDefinition, singleCurve)
				]
			}
		}

		if (!(geom instanceof GeometryCollection)) {
			// geometries that are no collections are always OK
			return [geometry]
		}

		if (!collections || geom.class == GeometryCollection) {
			// collections are not allowed or heterogeneous collection -> split and collect
			List<GeometryProperty<? extends Geometry>> collected = []
			for (int i = 0; i < geom.numGeometries; i++) {
				collected.addAll(toSimpleGeometries(
						new DefaultGeometryProperty(geometry.CRSDefinition, geom.getGeometryN(i)),
						collections))
			}
			return collected
		}
		else {
			// collection OK as it is
			return [geometry]
		}
	}

}
