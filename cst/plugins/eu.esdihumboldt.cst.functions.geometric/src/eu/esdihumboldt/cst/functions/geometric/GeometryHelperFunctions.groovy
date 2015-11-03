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

import eu.esdihumboldt.cst.functions.geometric.aggregate.AggregateTransformation
import eu.esdihumboldt.cst.functions.groovy.helper.spec.*
import eu.esdihumboldt.cst.functions.groovy.helper.spec.impl.HelperFunctionArgument
import eu.esdihumboldt.cst.functions.groovy.helper.spec.impl.HelperFunctionSpecification
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException
import eu.esdihumboldt.hale.common.instance.geometry.GeometryFinder
import eu.esdihumboldt.hale.common.instance.helper.DepthFirstInstanceTraverser
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraverser
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import groovy.transform.CompileStatic

/**
 * Geometry helper functions for Groovy scripts.
 * 
 * @author Simon Templer
 */
class GeometryHelperFunctions {

	public static final String GEOM_HOLDER_DESC = 'A geometry, geometry property or instance holding a geometry'

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

}
