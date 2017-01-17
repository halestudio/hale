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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.InterpolationHelper;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.GMLGeometryUtil;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryNotSupportedException;
import eu.esdihumboldt.hale.io.gml.geometry.constraint.GeometryFactory;

/**
 * Handler for linestring geometries
 * 
 * @author Patrick Lieb
 */
public class LineStringHandler extends FixedConstraintsGeometryHandler {

	private static final String LINE_STRING_TYPE = "LineStringType";

	private static final String LINE_STRING_SEGMENT_TYPE = "LineStringSegmentType";

	// XXX support for curve types is not optimal (different number of feature
	// members needed)

	// private static final String ARC_TYPE = "ArcType";

	private static final String ARC_BY_BULGE_TYPE = "ArcByBulgeType";

//	private static final String ARC_BY_CENTER_POINT_TYPE = "ArcByCenterPointType";

	// private static final String ARC_STRING_TYPE = "ArcStringType";

	private static final String ARC_STRING_BY_BULGE_TYPE = "ArcStringByBulgeType";

	private static final String BEZIER_TYPE = "BezierType";

	private static final String BSPLINE_TYPE = "BSplineType";

	// private static final String CIRCLE_TYPE = "CircleType";

//	private static final String CIRCLE_BY_CENTER_TYPE = "CircleByCenterType";

	private static final String CUBIC_SPLINE = "CubicSplineType";

	private static final String GEODESIC_TYPE = "Geodesic_Type";

	private static final String GEODESIC_STRING_TYPE = "GeodesicStringType";

	// XXX support for Triangle and Rectangle is not optimal (only exterior and
	// outerBounderIs needed)

	@Override
	public Object createGeometry(Instance instance, int srsDimension, IOProvider reader)
			throws GeometryNotSupportedException {
		LineString line = null;
		PointHandler handler = new PointHandler();

		// XXX support for different types of line strings in one instance (we
		// support only one type per instance!)

		// to parse coordinates of a line string
		// for use with GML 2, 3, 3.1, 3.2
		Collection<Object> values = PropertyResolver.getValues(instance, "coordinates", false);
		if (values != null && !values.isEmpty()) {
			Object value = values.iterator().next();
			if (value instanceof Instance) {
				try {
					Coordinate[] cs = GMLGeometryUtil.parseCoordinates((Instance) value);
					if (cs != null && cs.length > 0) {
						line = getGeometryFactory().createLineString(moveCoordinates(cs, reader));
					}
				} catch (ParseException e) {
					throw new GeometryNotSupportedException("Could not parse coordinates", e);
				}
			}
		}

		// to parse several pos of a line string
		// for use with GML 3, 3.2
		if (line == null) {
			values = PropertyResolver.getValues(instance, "pos", false);
			if (values != null && !values.isEmpty()) {
				Iterator<Object> iterator = values.iterator();
				List<Coordinate> cs = new ArrayList<Coordinate>();
				while (iterator.hasNext()) {
					Object value = iterator.next();
					if (value instanceof Instance) {
						Coordinate c = GMLGeometryUtil.parseDirectPosition((Instance) value);
						if (c != null) {
							cs.add(c);
						}
					}
				}
				Coordinate[] coords = moveCoordinates(cs.toArray(new Coordinate[cs.size()]),
						reader);
				line = getGeometryFactory().createLineString(coords);
			}
		}

		// to parse a posList of a line string
		// for use with GML 3.1, 3.2
		if (line == null) {
			values = PropertyResolver.getValues(instance, "posList", false);
			if (values != null && !values.isEmpty()) {
				Iterator<Object> iterator = values.iterator();
				Object value = iterator.next();
				if (value instanceof Instance) {
					Coordinate[] cs = GMLGeometryUtil.parsePosList((Instance) value, srsDimension);
					if (cs != null) {
						line = getGeometryFactory().createLineString(moveCoordinates(cs, reader));
					}
				}
			}
		}

		// to parse Point Representations of a line string
		// for use with GML 3, 3.1, 3.2

		if (line == null) {
			values = PropertyResolver.getValues(instance, "pointRep.Point", false);
			if (values != null && !values.isEmpty()) {
				Iterator<Object> iterator = values.iterator();
				List<Coordinate> cs = new ArrayList<Coordinate>();
				while (iterator.hasNext()) {
					Object value = iterator.next();
					if (value instanceof Instance) {
						try {
							@SuppressWarnings("unchecked")
							DefaultGeometryProperty<Point> point = (DefaultGeometryProperty<Point>) handler
									.createGeometry((Instance) value, srsDimension, reader);
							cs.add(point.getGeometry().getCoordinate());
						} catch (GeometryNotSupportedException e) {
							throw new GeometryNotSupportedException(
									"Could not parse Point Representation", e);
						}
					}
				}
				Coordinate[] coords = moveCoordinates(cs.toArray(new Coordinate[cs.size()]),
						reader);
				line = getGeometryFactory().createLineString(coords);
			}
		}

		// to parse Point Properties of a line string
		// for use with GML 3.1
		if (line == null) {
			values = PropertyResolver.getValues(instance, "pointProperty.Point", false);
			if (values != null && !values.isEmpty()) {
				Iterator<Object> iterator = values.iterator();
				List<Coordinate> cs = new ArrayList<Coordinate>();
				while (iterator.hasNext()) {
					Object value = iterator.next();
					if (value instanceof Instance) {
						try {
							@SuppressWarnings("unchecked")
							DefaultGeometryProperty<Point> point = (DefaultGeometryProperty<Point>) handler
									.createGeometry((Instance) value, srsDimension, reader);
							cs.add(point.getGeometry().getCoordinate());
						} catch (GeometryNotSupportedException e) {
							throw new GeometryNotSupportedException(
									"Could not parse Point Property", e);
						}
					}
				}
				Coordinate[] coords = moveCoordinates(cs.toArray(new Coordinate[cs.size()]),
						reader);
				line = getGeometryFactory().createLineString(coords);
			}
		}

		// to parse coord of a line string
		// for use with GML2, 3, 3.1, 3.2
		if (line == null) {
			values = PropertyResolver.getValues(instance, "coord", false);
			if (values != null && !values.isEmpty()) {
				Iterator<Object> iterator = values.iterator();
				List<Coordinate> cs = new ArrayList<Coordinate>();
				while (iterator.hasNext()) {
					Object value = iterator.next();
					if (value instanceof Instance) {
						Coordinate c = GMLGeometryUtil.parseCoord((Instance) value);
						if (c != null) {
							cs.add(c);
						}
					}
				}
				Coordinate[] coords = moveCoordinates(cs.toArray(new Coordinate[cs.size()]),
						reader);
				line = getGeometryFactory().createLineString(coords);
			}
		}

		if (line != null) {
			CRSDefinition crsDef = GMLGeometryUtil.findCRS(instance);
			return new DefaultGeometryProperty<LineString>(crsDef, line);
		}

		throw new GeometryNotSupportedException();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler#initConstraints()
	 */
	@Override
	protected Collection<? extends TypeConstraint> initConstraints() {
		Collection<TypeConstraint> constraints = new ArrayList<TypeConstraint>(2);

		constraints.add(Binding.get(GeometryProperty.class));
		constraints.add(GeometryType.get(LineString.class));

		constraints.add(new GeometryFactory(this));

		return constraints;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.AbstractGeometryHandler#initSupportedTypes()
	 */
	@Override
	protected Set<? extends QName> initSupportedTypes() {
		Set<QName> types = new HashSet<QName>();

		types.add(new QName(NS_GML, LINE_STRING_TYPE));
		types.add(new QName(NS_GML_32, LINE_STRING_TYPE));

		types.add(new QName(NS_GML, LINE_STRING_SEGMENT_TYPE));
		types.add(new QName(NS_GML_32, LINE_STRING_SEGMENT_TYPE));

//		types.add(new QName(NS_GML, ARC_TYPE));
//		types.add(new QName(NS_GML_32, ARC_TYPE));

		types.add(new QName(NS_GML, ARC_BY_BULGE_TYPE));
		types.add(new QName(NS_GML_32, ARC_BY_BULGE_TYPE));

//		types.add(new QName(NS_GML, ARC_BY_CENTER_POINT_TYPE));
//		types.add(new QName(NS_GML_32, ARC_BY_CENTER_POINT_TYPE));

//		types.add(new QName(NS_GML, ARC_STRING_TYPE));
//		types.add(new QName(NS_GML_32, ARC_STRING_TYPE));

		types.add(new QName(NS_GML, ARC_STRING_BY_BULGE_TYPE));
		types.add(new QName(NS_GML_32, ARC_STRING_BY_BULGE_TYPE));

		types.add(new QName(NS_GML, BEZIER_TYPE));
		types.add(new QName(NS_GML_32, BEZIER_TYPE));

		types.add(new QName(NS_GML, BSPLINE_TYPE));
		types.add(new QName(NS_GML_32, BSPLINE_TYPE));

//		types.add(new QName(NS_GML, CIRCLE_TYPE));
//		types.add(new QName(NS_GML_32, CIRCLE_TYPE));

//		types.add(new QName(NS_GML, CIRCLE_BY_CENTER_TYPE));
//		types.add(new QName(NS_GML_32, CIRCLE_BY_CENTER_TYPE));

		types.add(new QName(NS_GML, CUBIC_SPLINE));
		types.add(new QName(NS_GML_32, CUBIC_SPLINE));

		types.add(new QName(NS_GML, GEODESIC_TYPE));
		types.add(new QName(NS_GML_32, GEODESIC_TYPE));

		types.add(new QName(NS_GML, GEODESIC_STRING_TYPE));
		types.add(new QName(NS_GML_32, GEODESIC_STRING_TYPE));

//		types.add(new QName(NS_GML, ENVELOPE_TYPE));
//		types.add(new QName(NS_GML_32, ENVELOPE_TYPE));
//		
//		types.add(new QName(NS_GML, ENVELOPE_WITH_TIME_PERIOD_TYPE));
//		types.add(new QName(NS_GML_32, ENVELOPE_WITH_TIME_PERIOD_TYPE));

		return types;
	}

	private Coordinate[] moveCoordinates(Coordinate[] coordinates, IOProvider reader) {
		return isInterpolated() ? coordinates
				: InterpolationHelper.moveCoordinates(reader, coordinates);
	}

	/**
	 * States if the handler created interpolated geometries.
	 * 
	 * @return true if required else false
	 */
	protected boolean isInterpolated() {
		return false;
	}

}
