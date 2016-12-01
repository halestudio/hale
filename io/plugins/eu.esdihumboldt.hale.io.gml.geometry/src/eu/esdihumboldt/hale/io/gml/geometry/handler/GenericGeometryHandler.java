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

package eu.esdihumboldt.hale.io.gml.geometry.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.GeometryFinder;
import eu.esdihumboldt.hale.common.instance.helper.DepthFirstInstanceTraverser;
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraverser;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ElementType;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.io.gml.geometry.AbstractGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.GMLGeometryUtil;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryNotSupportedException;
import eu.esdihumboldt.hale.io.gml.geometry.constraint.GeometryFactory;

/**
 * Generic geometry handler for AbstractGeometryType.
 * 
 * @author Simon Templer
 */
public class GenericGeometryHandler extends FixedConstraintsGeometryHandler {

	/**
	 * Wraps a {@link CRSDefinition}.
	 */
	public static class CRSWrapper {

		private final CRSDefinition crsDef;

		/**
		 * Create a CRS wrapper
		 * 
		 * @param crsDefinition the CRS definition
		 */
		public CRSWrapper(CRSDefinition crsDefinition) {
			this.crsDef = crsDefinition;
		}

		/**
		 * Get the contained CRS definition.
		 * 
		 * @return the CRS definition
		 */
		public CRSDefinition getCrsDef() {
			return crsDef;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((crsDef == null) ? 0 : crsDef.hashCode());
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
			CRSWrapper other = (CRSWrapper) obj;
			if (crsDef == null) {
				if (other.crsDef != null)
					return false;
			}
			else if (!crsDef.equals(other.crsDef))
				return false;
			return true;
		}

	}

	private static final String ABSTRACT_GEOMETRY_TYPE = "AbstractGeometryType";

	/**
	 * @see AbstractGeometryHandler#initSupportedTypes()
	 */
	@Override
	protected Set<? extends QName> initSupportedTypes() {
		Set<QName> types = new HashSet<QName>();

		types.add(new QName(NS_GML, ABSTRACT_GEOMETRY_TYPE));
		types.add(new QName(NS_GML_32, ABSTRACT_GEOMETRY_TYPE));

		return types;
	}

	/**
	 * @see FixedConstraintsGeometryHandler#initConstraints()
	 */
	@Override
	protected Collection<? extends TypeConstraint> initConstraints() {
		Collection<TypeConstraint> constraints = new ArrayList<TypeConstraint>(3);

		// binding is collection, as we can't be sure that all contained
		// geometries share the same CRS
		constraints.add(Binding.get(Collection.class));
		// set element type binding to GeometryProperty
		constraints.add(ElementType.get(GeometryProperty.class));
		// geometry binding is Geometry, as we can't narrow it down further
		constraints.add(GeometryType.get(Geometry.class));
		// set geometry factory constraint
		constraints.add(new GeometryFactory(this));

		return constraints;
	}

	/**
	 * @see GeometryHandler#createGeometry(Instance, int, IOProvider)
	 */
	@Override
	public Collection<GeometryProperty<?>> createGeometry(Instance instance, int srsDimension,
			IOProvider reader) throws GeometryNotSupportedException {
		CRSDefinition defaultCrsDef = GMLGeometryUtil.findCRS(instance);

		// depth first traverser that on cancel continues traversal but w/o the
		// children of the current object
		InstanceTraverser traverser = new DepthFirstInstanceTraverser(true);

		GeometryFinder geoFind = new GeometryFinder(defaultCrsDef);

		traverser.traverse(instance, geoFind);

		return createGeometry(instance, geoFind.getGeometries(), defaultCrsDef, reader);
	}

	/**
	 * Create a geometry value from a given instance.
	 * 
	 * @param instance the instance
	 * @param childGeometries the child geometries found in the instance
	 * @param defaultCrs the definition of the default CRS for this instance
	 * @param reader the IO provider
	 * @return the geometry value derived from the instance, the return type
	 *         should match the {@link Binding} created in
	 *         {@link #getTypeConstraints(TypeDefinition)}.
	 * @throws GeometryNotSupportedException if the type definition doesn't
	 *             represent a geometry type supported by the handler
	 */
	@SuppressWarnings("unused")
	protected Collection<GeometryProperty<?>> createGeometry(Instance instance,
			List<GeometryProperty<?>> childGeometries, CRSDefinition defaultCrs, IOProvider reader)
					throws GeometryNotSupportedException {

		List<Geometry> geomList = new ArrayList<Geometry>();

		Class<? extends Geometry> commonGeomType = null;
		CRSWrapper commonCrs = null;
		boolean allowCombine = true;

		// collect geometries and check for common geometry type and CRS
		// TODO partition based on CRS?
		for (GeometryProperty<?> geomProp : childGeometries) {
			if (geomProp.getGeometry() instanceof GeometryCollection) {
				GeometryCollection geomCollection = (GeometryCollection) geomProp.getGeometry();
				for (int i = 0; i < geomCollection.getNumGeometries(); i++) {
					// find the common geometry class
					Class<? extends Geometry> geometryType = geomCollection.getGeometryN(i)
							.getClass();
					if (commonGeomType == null) {
						commonGeomType = geometryType;
					}
					else if (!commonGeomType.equals(geometryType)) {
						// TODO determine common type in inheritance?
						commonGeomType = Geometry.class;
					}
					geomList.add(geomCollection.getGeometryN(i));
				}
			}
			else {
				// find the common geometry class
				Class<? extends Geometry> geometryType = geomProp.getGeometry().getClass();
				if (commonGeomType == null) {
					commonGeomType = geometryType;
				}
				else if (!commonGeomType.equals(geometryType)) {
					// TODO determine common type in inheritance?
					commonGeomType = Geometry.class;
				}
				geomList.add(geomProp.getGeometry());
			}

			// check common CRS
			CRSWrapper crs = new CRSWrapper(geomProp.getCRSDefinition());
			if (commonCrs == null) {
				commonCrs = crs;
			}
			else if (!commonCrs.equals(crs)) {
				allowCombine = false;
			}
		}

		if (allowCombine && commonGeomType != null) {
			Geometry geom = null;
			if (commonGeomType.equals(Polygon.class)) {
				// create a MultiPolygon
				Polygon[] polygons = new Polygon[geomList.size()];
				for (int i = 0; i < geomList.size(); i++) {
					polygons[i] = (Polygon) geomList.get(i);
				}
				geom = combine(polygons);
			}
			else if (commonGeomType.equals(LineString.class)) {
				// create a MultiLineString
				LineString[] lines = new LineString[geomList.size()];
				for (int i = 0; i < geomList.size(); i++) {
					lines[i] = (LineString) geomList.get(i);
				}
				geom = combine(lines);
			}
			else if (commonGeomType.equals(Point.class)) {
				// create a MultiPoint
				Point[] points = new Point[geomList.size()];
				for (int i = 0; i < geomList.size(); i++) {
					points[i] = (Point) geomList.get(i);
				}
				geom = combine(points);
			}
			if (geom != null) {
				// returned combined property
				CRSDefinition crs = (commonCrs != null && commonCrs.getCrsDef() != null)
						? (commonCrs.getCrsDef()) : (defaultCrs);
				return Collections.<GeometryProperty<?>> singleton(
						new DefaultGeometryProperty<Geometry>(crs, geom));
			}
		}

		// fall-back: return a collection of geometry properties
		if (childGeometries.isEmpty()) {
			return null;
		}
		return childGeometries;
	}

	/**
	 * Combine points to a geometry.
	 * 
	 * @param points the points to combine
	 * @return the combined geometry
	 */
	protected Geometry combine(Point[] points) {
		return getGeometryFactory().createMultiPoint(points);
	}

	/**
	 * Combine lines to a geometry.
	 * 
	 * @param lines the lines to combine
	 * @return the combined geometry
	 */
	protected Geometry combine(LineString[] lines) {
		return getGeometryFactory().createMultiLineString(lines);
	}

	/**
	 * Combine polygons to a geometry.
	 * 
	 * @param polygons the polygons to combine
	 * @return the combined geometry
	 */
	protected Geometry combine(Polygon[] polygons) {
		return getGeometryFactory().createMultiPolygon(polygons);
	}
}
