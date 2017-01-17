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

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.convert.ConversionUtil;
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
import eu.esdihumboldt.util.geometry.interpolation.InterpolationAlgorithm;
import eu.esdihumboldt.util.geometry.interpolation.model.Angle;
import eu.esdihumboldt.util.geometry.interpolation.model.Arc;
import eu.esdihumboldt.util.geometry.interpolation.model.impl.ArcByCenterPointImpl;

/**
 * Handler for Arc by center point geometries.
 * 
 * @author Arun Verma
 * @author Simon Templer
 */
public class ArcByCenterPointHandler extends FixedConstraintsGeometryHandler {

	private static final String ARC_BY_CENTER_POINT_TYPE = "ArcByCenterPointType";

	private static final ALogger log = ALoggerFactory.getLogger(ArcByCenterPointHandler.class);

	@Override
	public Object createGeometry(Instance instance, int srsDimension, IOProvider reader)
			throws GeometryNotSupportedException {
		PointHandler handler = new PointHandler();

		// XXX support for different types of line strings in one instance (we
		// support only one type per instance!)

		Coordinate[] controlCoord = null;
		double radius = 0;
		double startAngle = 0;
		double endAngle = 0;

		// to parse coordinates of a line string
		// for use with GML 2, 3, 3.1, 3.2
		Collection<Object> values = PropertyResolver.getValues(instance, "coordinates", false);
		if (values != null && !values.isEmpty()) {
			Object value = values.iterator().next();
			if (value instanceof Instance) {
				try {
					controlCoord = GMLGeometryUtil.parseCoordinates((Instance) value);
				} catch (ParseException e) {
					throw new GeometryNotSupportedException("Could not parse coordinates", e);
				}
			}
		}

		// to parse several pos of a line string
		// for use with GML 3, 3.2
		if (controlCoord == null || controlCoord.length == 0) {
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
				controlCoord = cs.toArray(new Coordinate[cs.size()]);
			}
		}

		// to parse a posList of a line string
		// for use with GML 3.1, 3.2
		if (controlCoord == null || controlCoord.length == 0) {
			values = PropertyResolver.getValues(instance, "posList", false);
			if (values != null && !values.isEmpty()) {
				Iterator<Object> iterator = values.iterator();
				Object value = iterator.next();
				if (value instanceof Instance) {
					controlCoord = GMLGeometryUtil.parsePosList((Instance) value, srsDimension);
				}
			}
		}

		// to parse Point Representations of a line string
		// for use with GML 3, 3.1, 3.2

		if (controlCoord == null || controlCoord.length == 0) {
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
				controlCoord = cs.toArray(new Coordinate[cs.size()]);
			}
		}

		// to parse Point Properties of a line string
		// for use with GML 3.1
		if (controlCoord == null || controlCoord.length == 0) {
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
				controlCoord = cs.toArray(new Coordinate[cs.size()]);
			}
		}

		// to parse coord of a line string
		// for use with GML2, 3, 3.1, 3.2
		if (controlCoord == null || controlCoord.length == 0) {
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
				controlCoord = cs.toArray(new Coordinate[cs.size()]);
			}
		}

		values = PropertyResolver.getValues(instance, "radius", false);
		if (values != null && !values.isEmpty()) {
			Object value = values.iterator().next();
			if (value instanceof Instance) {
				// ##TODO :: need to check with real time data
				radius = ConversionUtil.getAs(((Instance) value).getValue(), Double.class);

			}
		}

		values = PropertyResolver.getValues(instance, "startAngle", false);
		if (values != null && !values.isEmpty()) {
			Object value = values.iterator().next();
			if (value != null) {
				if (value instanceof Instance) {
					// ##TODO: handle if value comes in degree, minute and
					// second format. Need to check with real time data
					startAngle = ConversionUtil.getAs(((Instance) value).getValue(), Double.class);

				}
			}
		}

		values = PropertyResolver.getValues(instance, "endAngle", false);
		if (values != null && !values.isEmpty()) {
			Object value = values.iterator().next();
			if (value != null) {
				if (value instanceof Instance) {
					// ##TODO: handle if value comes in degree, minute and
					// second format. Need to check with real time data
					endAngle = ConversionUtil.getAs(((Instance) value).getValue(), Double.class);
				}
			}
		}

		if (controlCoord != null && controlCoord.length != 0 && radius != 0) {
			CRSDefinition crsDef = GMLGeometryUtil.findCRS(instance);

			// create Arc
			// FIXME verify how arc should be created from information in GML
			boolean clockwise = endAngle - startAngle < 0;
			Arc arc = new ArcByCenterPointImpl(controlCoord[0], radius,
					Angle.fromDegrees(startAngle), Angle.fromDegrees(endAngle), clockwise);

			// get interpolation algorithm
			InterpolationAlgorithm interpol = InterpolationHelper.getInterpolation(reader,
					getGeometryFactory());
			LineString interpolatedArc = interpol.interpolateArc(arc);

			if (interpolatedArc == null) {
				log.error("Arc could be not interpolated to Linestring");
				return null;
			}
			return new DefaultGeometryProperty<LineString>(crsDef, interpolatedArc);

		}

		throw new GeometryNotSupportedException();
	}

	@Override
	protected Collection<? extends TypeConstraint> initConstraints() {
		Collection<TypeConstraint> constraints = new ArrayList<TypeConstraint>(2);

		constraints.add(Binding.get(GeometryProperty.class));
		constraints.add(GeometryType.get(LineString.class));

		constraints.add(new GeometryFactory(this));

		return constraints;
	}

	@Override
	protected Set<? extends QName> initSupportedTypes() {
		Set<QName> types = new HashSet<QName>();

		types.add(new QName(NS_GML, ARC_BY_CENTER_POINT_TYPE));
		types.add(new QName(NS_GML_32, ARC_BY_CENTER_POINT_TYPE));

		return types;
	}

}
