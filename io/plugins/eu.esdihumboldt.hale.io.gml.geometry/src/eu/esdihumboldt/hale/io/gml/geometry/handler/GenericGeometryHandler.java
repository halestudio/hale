/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
	 * @see GeometryHandler#createGeometry(Instance, int)
	 */
	@Override
	public Object createGeometry(Instance instance, int srsDimension)
			throws GeometryNotSupportedException {
		CRSDefinition defaultCrsDef = GMLGeometryUtil.findCRS(instance);

		// depth first traverser that on cancel continues traversal but w/o the
		// children of the current object
		InstanceTraverser traverser = new DepthFirstInstanceTraverser(true);

		GeometryFinder geoFind = new GeometryFinder(defaultCrsDef);

		traverser.traverse(instance, geoFind);

		return createGeometry(instance, geoFind.getGeometries(), defaultCrsDef);
	}

	/**
	 * Create a geometry value from a given instance.
	 * 
	 * @param instance the instance
	 * @param childGeometries the child geometries found in the instance
	 * @param defaultCrs the definition of the default CRS for this instance
	 * @return the geometry value derived from the instance, the return type
	 *         should match the {@link Binding} created in
	 *         {@link #getTypeConstraints(TypeDefinition)}.
	 * @throws GeometryNotSupportedException if the type definition doesn't
	 *             represent a geometry type supported by the handler
	 */
	@SuppressWarnings("unused")
	protected Object createGeometry(Instance instance, List<GeometryProperty<?>> childGeometries,
			CRSDefinition defaultCrs) throws GeometryNotSupportedException {

		List<Geometry> geomList = new ArrayList<Geometry>();

		Class<? extends Geometry> commonGeomType = null;
		// TODO also check for common crs? no equals for CRSDefinitions
		// implemented! important for merging geometries

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

		}

		if (commonGeomType != null) {
			Geometry geom = null;
			if (commonGeomType.equals(Polygon.class)) {
				// create a MultiPolygon
				Polygon[] polygons = new Polygon[geomList.size()];
				for (int i = 0; i < geomList.size(); i++) {
					polygons[i] = (Polygon) geomList.get(i);
				}
				geom = getGeometryFactory().createMultiPolygon(polygons);
			}
			else if (commonGeomType.equals(LineString.class)) {
				// create a MultiLineString
				LineString[] lines = new LineString[geomList.size()];
				for (int i = 0; i < geomList.size(); i++) {
					lines[i] = (LineString) geomList.get(i);
				}
				geom = getGeometryFactory().createMultiLineString(lines);
			}
			else if (commonGeomType.equals(Point.class)) {
				// create a MultiPoint
				Point[] points = new Point[geomList.size()];
				for (int i = 0; i < geomList.size(); i++) {
					points[i] = (Point) geomList.get(i);
				}
				geom = getGeometryFactory().createMultiPoint(points);
			}
			if (geom != null) {
				// returned combined property
				return Collections
						.singleton(new DefaultGeometryProperty<Geometry>(defaultCrs, geom));
			}
		}

		// fall-back: return a collection of geometry properties
		if (childGeometries.isEmpty()) {
			return null;
		}
		return childGeometries;
	}

}
