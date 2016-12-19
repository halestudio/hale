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

package eu.esdihumboldt.hale.io.gml.geometry._disabled_handler_;

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
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.io.gml.geometry.GMLGeometryUtil;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryNotSupportedException;
import eu.esdihumboldt.hale.io.gml.geometry.InterpolationSupportedGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.constraint.GeometryFactory;
import eu.esdihumboldt.hale.io.gml.geometry.handler.PointHandler;
import eu.esdihumboldt.util.geometry.interpolation.CircleByCenterPointInterpolation;
import eu.esdihumboldt.util.geometry.interpolation.Interpolation;

/**
 * Handler for circle by center point geometries
 * 
 * @author Arun
 */
public class CircleByCenterPointHandler extends InterpolationSupportedGeometryHandler {

	private static final String CIRCLE_BY_CENTER_POINT_TYPE = "CircleByCenterPointType";

	private static final ALogger log = ALoggerFactory.getLogger(CircleByCenterPointHandler.class);

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler#createGeometry(eu.esdihumboldt.hale.common.instance.model.Instance,
	 *      int, eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public Object createGeometry(Instance instance, int srsDimension, IOProvider reader)
			throws GeometryNotSupportedException {
		PointHandler handler = new PointHandler();

		// XXX support for different types of line strings in one instance (we
		// support only one type per instance!)

		Coordinate[] controlCoord = null;
		double radius = 0;

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

		if (controlCoord != null && controlCoord.length != 0 && radius != 0) {
			CRSDefinition crsDef = GMLGeometryUtil.findCRS(instance);

			// get interpolation required parameter
			getInterpolationRequiredParameter(reader);

			Interpolation<LineString> interpolation = new CircleByCenterPointInterpolation(
					controlCoord[0], radius, getMaxPositionalError());
			LineString interpolatedCircle = interpolation.interpolateRawGeometry();
			if (interpolatedCircle == null) {
				log.error("Circle could be not interpolated to Linestring");
				return null;
			}
			return new DefaultGeometryProperty<LineString>(crsDef, interpolatedCircle);

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

		types.add(new QName(NS_GML, CIRCLE_BY_CENTER_POINT_TYPE));
		types.add(new QName(NS_GML_32, CIRCLE_BY_CENTER_POINT_TYPE));

		return types;
	}

}
