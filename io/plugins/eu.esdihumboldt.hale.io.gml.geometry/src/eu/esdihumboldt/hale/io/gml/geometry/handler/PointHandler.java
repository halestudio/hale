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
import java.util.Set;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
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
import eu.esdihumboldt.hale.io.gml.geometry.InterpolationSupportedGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.constraint.GeometryFactory;

/**
 * Handler for point geometries.
 * 
 * @author Simon Templer
 */
public class PointHandler extends InterpolationSupportedGeometryHandler {

	private static final String POINT_TYPE = "PointType";

	/**
	 * @see GeometryHandler#createGeometry(Instance, int, IOProvider)
	 */
	@Override
	public Object createGeometry(Instance instance, int srsDimension, IOProvider reader)
			throws GeometryNotSupportedException {
		Point point = null;

		// Point is either defined by a CoordinatesType named coordinates
		Collection<Object> values = PropertyResolver.getValues(instance, "coordinates", false);
		if (values != null && !values.isEmpty()) {
			Object value = values.iterator().next();
			if (value instanceof Instance) {
				try {
					Coordinate[] cs = GMLGeometryUtil.parseCoordinates((Instance) value);
					if (cs != null && cs.length > 0) {
						cs = moveToUniversalGrid(new Coordinate[] { cs[0] }, reader);
						point = getGeometryFactory().createPoint(cs[0]);
					}
				} catch (ParseException e) {
					throw new GeometryNotSupportedException("Could not parse coordinates", e);
				}
			}
		}

		// or by a DirectPositionType named pos
		if (point == null) {
			values = PropertyResolver.getValues(instance, "pos", false);
			if (values != null && !values.isEmpty()) {
				Object value = values.iterator().next();
				if (value instanceof Instance) {
					Coordinate c = GMLGeometryUtil.parseDirectPosition((Instance) value);
					if (c != null) {
						c = moveToUniversalGrid(new Coordinate[] { c }, reader)[0];
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
						c = moveToUniversalGrid(new Coordinate[] { c }, reader)[0];
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
		Collection<TypeConstraint> constraints = new ArrayList<TypeConstraint>(2);

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
