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
 * Handler for Curve Geometries
 * 
 * @author Kevin Mais
 */
public class CurveHandler extends FixedConstraintsGeometryHandler {

	private static final String CURVE_TYPE = "CurveType";

	/**
	 * @see GeometryHandler#createGeometry(Instance)
	 */
	@Override
	public Object createGeometry(Instance instance)
			throws GeometryNotSupportedException {
		Curve curve = null;
		Point[] pointArr;
		PointHandler handler = new PointHandler();

		// Curve is either defined by a CoordinatesType named coordinates
		// GML 3.0, 3.1, 3.2
		Collection<Object> values = PropertyResolver.getValues(instance,
				"segments.LineStringSegment", false);
		if (values != null && !values.isEmpty()) {
			Collection<Point> points = new ArrayList<Point>();
			while (values.iterator().hasNext()) {
				Object value = values.iterator().next();
				if (value instanceof Instance) {
					try {
						DefaultGeometryProperty<Point> point = (DefaultGeometryProperty<Point>) handler
								.createGeometry((Instance) value);
						points.add(point.getGeometry());

					} catch (ParseException e) {
						throw new GeometryNotSupportedException(
								"Could not parse coordinates", e);
					}
				}
			}

			pointArr = points.toArray(new Point[points.size()]);
		}

		curve = getGeometryFactory().createCurve(pointArr);

		if (curve != null) {
			CRSDefinition crsDef = GMLGeometryUtil.findCRS(instance);
			return new DefaultGeometryProperty<Curve>(crsDef, curve);
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
		constraints.add(GeometryType.get(Curve.class));
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

		types.add(new QName(NS_GML, CURVE_TYPE));
		types.add(new QName(NS_GML_32, CURVE_TYPE));

		return types;
	}

}
