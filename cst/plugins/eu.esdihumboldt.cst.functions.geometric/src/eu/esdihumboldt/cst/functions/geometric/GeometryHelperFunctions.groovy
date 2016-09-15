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

package eu.esdihumboldt.cst.functions.geometric;

import javax.annotation.Nullable

import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.LineString
import com.vividsolutions.jts.geom.MultiLineString
import com.vividsolutions.jts.geom.MultiPoint
import com.vividsolutions.jts.geom.MultiPolygon
import com.vividsolutions.jts.geom.Point
import com.vividsolutions.jts.geom.Polygon
import com.vividsolutions.jts.io.WKTReader

import eu.esdihumboldt.cst.functions.geometric.aggregate.AggregateTransformation
import eu.esdihumboldt.cst.functions.geometric.interiorpoint.InteriorPoint
import eu.esdihumboldt.cst.functions.groovy.helper.spec.*
import eu.esdihumboldt.cst.functions.groovy.helper.spec.impl.HelperFunctionArgument
import eu.esdihumboldt.cst.functions.groovy.helper.spec.impl.HelperFunctionSpecification
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty
import eu.esdihumboldt.hale.common.instance.geometry.GeometryFinder
import eu.esdihumboldt.hale.common.instance.helper.DepthFirstInstanceTraverser
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraverser
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import groovy.transform.CompileStatic

/**
 * Geometry helper functions for Groovy scripts.
 * 
 * @author Simon Templer
 */
class GeometryHelperFunctions {

	public static final String GEOM_HOLDER_DESC = 'A geometry, geometry property or instance holding a geometry'

	private static final GeometryFactory factory = new GeometryFactory()

	/**
	 * Specification for the centroid function
	 */
	public static final Specification _centroid_spec = SpecBuilder.newSpec( //
	description: 'Calculate the centroid of a geometry.',
	result: 'The Point geometry that is the centroid of the given geometry (wrapped in a GeometryProperty) or null.') { //
		geometry(GEOM_HOLDER_DESC) }

	/**
	 * Calculate the centroid of a given geometry.
	 * 
	 * @param geometryHolder the {@link Geometry}, {@link GeometryProperty} or
	 *            {@link Instance} holding a geometry
	 * @return the centroid of the geometry or <code>null</code>
	 */
	@CompileStatic
	@Nullable
	static GeometryProperty<? extends Geometry> _centroid(def geometryHolder) {
		GeometryProperty<?> result;
		try {
			result = Centroid.calculateCentroid(geometryHolder);
		} catch (TransformationException e) {
			// XXX what should the behavior be?
			return null;
		}

		if (!result.geometry || result.geometry.isEmpty()) {
			return null;
		}

		return result;
	}

	/**
	 * Specification for the interior point function
	 */
	public static final Specification _interiorPoint_spec = SpecBuilder.newSpec( //
	description: 'Computes an interior point of a geometry (up to 2D). An interior point is guaranteed to lie in the interior of the geometry, if it is possible to calculate such a point exactly. Otherwise, the point may lie on the boundary of the geometry  (e.g. if the geometry is a line).',
	result: 'A point geometry that lies within (or, if this is not possible, on the boundary) of the given geometry (wrapped in a GeometryProperty) or null.') { //
		geometry(GEOM_HOLDER_DESC) }

	/**
	 * Calculate an interior point of a given geometry.
	 *
	 * @param geometryHolder the {@link Geometry}, {@link GeometryProperty} or
	 *            {@link Instance} holding a geometry
	 * @return an interior point of the geometry or <code>null</code>
	 */
	@CompileStatic
	@Nullable
	static GeometryProperty<? extends Geometry> _interiorPoint(def geometryHolder) {
		GeometryProperty<?> result = InteriorPoint.calculateInteriorPoint(geometryHolder);

		if (!result.geometry || result.geometry.isEmpty()) {
			return null;
		}

		return result;
	}

	/**
	 * Specification for a _buffer function
	 */
	public static final HelperFunctionSpecification _buffer_spec = new HelperFunctionSpecification(
	"Calculate a buffer geometry from a given geometry.", "the buffered geometry (wrapped in a GeometryProperty) or null",
	new HelperFunctionArgument("geometry",
	"Geometry, geometry property or instance holding the geometry to calculate a buffer on"),
	new HelperFunctionArgument("distance",
	"Buffer distance", 0));

	/**
	 * Calculate a buffer from an existing geometry.
	 *
	 * @param args the function arguments
	 * @return the buffer geometry or <code>null</code>
	 */
	@CompileStatic
	@Nullable
	static GeometryProperty<? extends Geometry> _buffer(Map args) {
		GeometryProperty<?> result = NetworkExpansion.calculateBuffer(
				args.geometry, // the geometry holder
				args.distance as double, // buffer distance
				null)

		if (result && result.geometry && !result.geometry.isEmpty()) {
			result
		}
		else {
			null
		}
	}

	/**
	 * Specification for the find function
	 */
	public static final Specification _find_spec = SpecBuilder.newSpec( //
	description: 'Find a geometry in the given objects.',
	result: 'the first geometry found (wrapped in a GeometryProperty) or null.') { //
		objects('An object or a list of objects to search for geometries') }

	@CompileStatic
	static GeometryProperty<? extends Geometry> _find(def geometryHolder) {
		// depth first traverser that on cancel stops further traversal
		InstanceTraverser traverser = new DepthFirstInstanceTraverser(false)
		GeometryFinder geoFind = new GeometryFinder(null)

		if (geometryHolder instanceof Iterable<?>) {
			geometryHolder.each {
				traverser.traverse(it, geoFind)
			}
		}
		else {
			traverser.traverse(geometryHolder, geoFind)
		}

		List<GeometryProperty<?>> geoms = geoFind.getGeometries()

		if (geoms) {
			geoms[0]
		}
		else {
			null
		}
	}

	/**
	 * Specification for the find function
	 */
	public static final Specification _findAll_spec = SpecBuilder.newSpec( //
	description: 'Find geometries in the given objects.',
	result: 'the list of found geometries (each wrapped in a GeometryProperty)') { //
		objects('An object or a list of objects to search for geometries') }

	@CompileStatic
	static List<GeometryProperty<? extends Geometry>> _findAll(def geometryHolder) {
		// depth first traverser that on cancel continues traversal but w/o the
		// children of the current object
		InstanceTraverser traverser = new DepthFirstInstanceTraverser(false)
		GeometryFinder geoFind = new GeometryFinder(null)

		if (geometryHolder instanceof Iterable<?>) {
			geometryHolder.each {
				traverser.traverse(it, geoFind)
			}
		}
		else {
			traverser.traverse(geometryHolder, geoFind)
		}

		geoFind.getGeometries()
	}

	/**
	 * Specification for the aggregate function
	 */
	public static final Specification _aggregate_spec = SpecBuilder.newSpec( //
	description: 'Aggregate geometries in the given objects.',
	result: 'the aggregated geometry (wrapped in a GeometryProperty) or null') { //
		geometries('A single or multiple (as a list/iterable) geometries, geometry properties or instances holding a geometry') }

	@CompileStatic
	static GeometryProperty<? extends Geometry> _aggregate(def geometryHolders) {
		Iterable<?> geoms;
		if (geometryHolders == null) {
			return null;
		}
		else if (geometryHolders instanceof Iterable) {
			geoms = (Iterable<?>) geometryHolders;
		}
		else {
			geoms = Collections.singleton(geometryHolders);
		}
		try {
			return AggregateTransformation.aggregateGeometries(geoms, null, null)
		} catch (NoResultException e) {
			return null
		}
	}

	/**
	 * Specification for the findPolygons function
	 */
	public static final Specification _findPolygons_spec = SpecBuilder.newSpec( //
	description: 'Find polygon or multi-polygon geometries in the given objects.',
	result: 'the list of found geometries (each wrapped in a GeometryProperty)') { //
		objects('An object or a list of objects to search for geometries') }

	@CompileStatic
	static Collection<GeometryProperty<? extends Geometry>> _findPolygons(def geometryHolders) {
		List<GeometryProperty<? extends Geometry>> all = _findAll(geometryHolders)

		return all.findAll { prop ->
			Geometry geom = prop.getGeometry()
			return geom instanceof Polygon || geom instanceof MultiPolygon
		}
	}

	/**
	 * Specification for the findLines function
	 */
	public static final Specification _findLines_spec = SpecBuilder.newSpec( //
	description: 'Find line or multi-line geometries in the given objects.',
	result: 'the list of found geometries (each wrapped in a GeometryProperty)') { //
		objects('An object or a list of objects to search for geometries') }

	@CompileStatic
	static Collection<GeometryProperty<? extends Geometry>> _findLines(def geometryHolders) {
		List<GeometryProperty<? extends Geometry>> all = _findAll(geometryHolders)

		return all.findAll { prop ->
			Geometry geom = prop.getGeometry()
			return geom instanceof LineString || geom instanceof MultiLineString
		}
	}

	/**
	 * Specification for the findPoints function
	 */
	public static final Specification _findPoints_spec = SpecBuilder.newSpec( //
	description: 'Find point or multi-point geometries in the given objects.',
	result: 'the list of found geometries (each wrapped in a GeometryProperty)') { //
		objects('An object or a list of objects to search for geometries') }

	@CompileStatic
	static Collection<GeometryProperty<? extends Geometry>> _findPoints(def geometryHolders) {
		List<GeometryProperty<? extends Geometry>> all = _findAll(geometryHolders)

		return all.findAll { prop ->
			Geometry geom = prop.getGeometry()
			return geom instanceof Point || geom instanceof MultiPoint
		}
	}

	/**
	 * Specification for the splitMulti function
	 */
	public static final Specification _splitMulti_spec = SpecBuilder.newSpec( //
	description: 'Split multi-geometries into separate geometries.',
	result: 'the list of single geometries (each wrapped in a GeometryProperty)') { //
		geometries('A single or multiple (as a list/iterable) geometries, geometry properties or instances holding a geometry') }

	@CompileStatic
	static Collection<GeometryProperty<? extends Geometry>> _splitMulti(def geometryHolders) {
		List<GeometryProperty<? extends Geometry>> all = _findAll(geometryHolders)

		return all.collectMany { prop ->
			Geometry geom = prop.getGeometry()
			if (geom.getNumGeometries() > 1) {
				Deque<Geometry> multis = new ArrayDeque<>()
				multis << geom
				def results = []
				while (!multis.empty) {
					Geometry multi = multis.pop()
					for (int i = 0; i < multi.getNumGeometries(); i++) {
						Geometry single = multi.getGeometryN(i)
						if (single.getNumGeometries() > 1) {
							// handle later
							multis << single
						}
						else {
							// create geometry property
							DefaultGeometryProperty<?> singleProp = new DefaultGeometryProperty<Geometry>(
									prop.getCRSDefinition(), single)
							results << singleProp
						}
					}
				}
				results
			}
			else {
				// as is
				[prop]
			}
		}
	}

	/**
	 * Specification for the with function
	 */
	public static final Specification _with_spec = SpecBuilder.newSpec( //
	description: 'Create a geometry property from a given geometry and CRS.',
	result: 'the geometry wrapped in a GeometryProperty and with the given CRS associated') {
		//
		geometry('A geometry object, a WKT geometry definition, a geometry property or an instance holding a geometry') //
		crs('the coordinate reference system definition object or code') }

	@CompileStatic
	static GeometryProperty<? extends Geometry> _with(Map args) {
		def geometryHolder = args.geometry
		def crs = args.crs

		GeometryProperty<? extends Geometry> geom
		if (geometryHolder instanceof String || geometryHolder instanceof GString) {
			WKTReader reader = new WKTReader(factory)
			Geometry g = reader.read(geometryHolder as String)
			if (!g) {
				geom == null
			}
			else {
				geom = new DefaultGeometryProperty<>(null, g)
			}
		}
		else {
			// search for geometry
			geom = _find(geometryHolder)
		}

		if (geom) {
			// apply CRS
			CRSDefinition crsDef
			if (crs == null) {
				crsDef = null
			}
			else if (crs instanceof CRSDefinition) {
				crsDef = (CRSDefinition) crs
			}
			else {
				crsDef = CRSHelperFunctions._from(code: crs)
			}

			new DefaultGeometryProperty<Geometry>(crsDef, geom.getGeometry())
		}
		else {
			// nothing here
			null
		}
	}

}
