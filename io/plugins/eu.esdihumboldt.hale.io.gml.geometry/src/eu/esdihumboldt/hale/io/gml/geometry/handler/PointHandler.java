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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.io.gml.geometry.AbstractGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.GMLGeometryUtil;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryNotSupportedException;
import eu.esdihumboldt.hale.io.gml.geometry.constraint.GeometryFactory;

/**
 * Handler for point geometries.
 * 
 * @author Simon Templer
 */
public class PointHandler extends FixedConstraintsGeometryHandler {

	private static final String POINT_TYPE = "PointType";

	/**
	 * @see GeometryHandler#createGeometry(Instance, int)
	 */
	@Override
	public Object createGeometry(Instance instance, int srsDimension)
			throws GeometryNotSupportedException {
		Point point = null;

		// Point is either defined by a CoordinatesType named coordinates
		Collection<Object> values = PropertyResolver.getValues(instance,
				"coordinates", false);
		if (values != null && !values.isEmpty()) {
			Object value = values.iterator().next();
			if (value instanceof Instance) {
				try {
					Coordinate[] cs = GMLGeometryUtil
							.parseCoordinates((Instance) value);
					if (cs != null && cs.length > 0) {
						point = getGeometryFactory().createPoint(cs[0]);
					}
				} catch (ParseException e) {
					throw new GeometryNotSupportedException(
							"Could not parse coordinates", e);
				}
			}
		}

		// or by a DirectPositionType named pos
		if (point == null) {
			values = PropertyResolver.getValues(instance, "pos", false);
			if (values != null && !values.isEmpty()) {
				Object value = values.iterator().next();
				if (value instanceof Instance) {
					Coordinate c = GMLGeometryUtil
							.parseDirectPosition((Instance) value);
					if (c != null) {
						point = getGeometryFactory().createPoint(c);
					}
				}
			}
		}

		// or even by a CoordType in GML 2
		if (point == null) {
			values = PropertyResolver.getValues(instance, "coord", false);
			if (values != null && !values.isEmpty()) {
				Object value = values.iterator().next();
				if (value instanceof Instance) {
					Coordinate c = GMLGeometryUtil.parseCoord((Instance) value);
					if (c != null) {
						point = getGeometryFactory().createPoint(c);
					}
				}
			}
		}

		if (point != null) {
			CRSDefinition crsDef = GMLGeometryUtil.findCRS(instance);
			return new DefaultGeometryProperty<Point>(crsDef, point);
		}

		throw new GeometryNotSupportedException(); // XXX
	}

	/**
	 * @see FixedConstraintsGeometryHandler#initConstraints()
	 */
	@Override
	protected Collection<? extends TypeConstraint> initConstraints() {
		Collection<TypeConstraint> constraints = new ArrayList<TypeConstraint>(
				2);

		// contains one point
		constraints.add(Binding.get(GeometryProperty.class));
		constraints.add(GeometryType.get(Point.class));
		// set geometry factory constraint
		constraints.add(new GeometryFactory(this));

		return constraints;
	}

	/**
	 * @see AbstractGeometryHandler#initSupportedTypes()
	 */
	@Override
	protected Set<? extends QName> initSupportedTypes() {
		Set<QName> types = new HashSet<QName>();

		types.add(new QName(NS_GML, POINT_TYPE));
		types.add(new QName(NS_GML_32, POINT_TYPE));

		return types;
	}

}
