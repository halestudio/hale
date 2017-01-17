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
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

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
import eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryNotSupportedException;
import eu.esdihumboldt.hale.io.gml.geometry.constraint.GeometryFactory;

/**
 * Handler for envelope geometries
 * 
 * @author Patrick Lieb
 */
public class EnvelopeHandler extends FixedConstraintsGeometryHandler {

	private static final String ENVELOPE_TYPE = "EnvelopeType";

	private static final String ENVELOPE_WITH_TIME_PERIOD_TYPE = "EnvelopeWithTimePeriodType";

	/**
	 * @see GeometryHandler#createGeometry(Instance, int, IOProvider)
	 */
	@Override
	public Object createGeometry(Instance instance, int srsDimension, IOProvider reader)
			throws GeometryNotSupportedException {

		MultiPoint envelope = null;
		List<Point> points = new ArrayList<Point>();

		Collection<Object> values = PropertyResolver.getValues(instance, "coordinates", false);
		if (values != null && !values.isEmpty()) {
			Iterator<Object> iterator = values.iterator();
			while (iterator.hasNext()) {
				Object value = iterator.next();
				if (value instanceof Instance) {
					try {
						Coordinate[] cs = GMLGeometryUtil.parseCoordinates((Instance) value);
						if (cs != null && cs.length > 0) {
							points.add(getGeometryFactory().createPoint(cs[0]));
						}
					} catch (ParseException e) {
						throw new GeometryNotSupportedException("Could not parse coordinates", e);
					}
				}
			}
		}

		if (points.isEmpty()) {
			values = PropertyResolver.getValues(instance, "pos", false);
			if (values != null && !values.isEmpty()) {
				Iterator<Object> iterator = values.iterator();
				while (iterator.hasNext()) {
					Object value = iterator.next();
					if (value instanceof Instance) {
						Coordinate c = GMLGeometryUtil.parseDirectPosition((Instance) value);
						if (c != null) {
							points.add(getGeometryFactory().createPoint(c));
						}
					}
				}
			}
		}

		if (points.isEmpty()) {
			values = PropertyResolver.getValues(instance, "coord", false);
			if (values != null && !values.isEmpty()) {
				Iterator<Object> iterator = values.iterator();
				while (iterator.hasNext()) {
					Object value = iterator.next();
					if (value instanceof Instance) {
						Coordinate c = GMLGeometryUtil.parseCoord((Instance) value);
						if (c != null) {
							points.add(getGeometryFactory().createPoint(c));
						}
					}
				}
			}
		}

		if (!points.isEmpty()) {
			Coordinate[] coordinates = new Coordinate[] { points.get(0).getCoordinate(),
					points.get(1).getCoordinate() };
			coordinates = InterpolationHelper.moveCoordinates(reader, coordinates);
			envelope = getGeometryFactory().createMultiPoint(coordinates);
		}

		// TODO support for lowerCorner/upperCorner

		if (envelope != null) {
			CRSDefinition crsDef = GMLGeometryUtil.findCRS(instance);
			return new DefaultGeometryProperty<MultiPoint>(crsDef, envelope);
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
		constraints.add(GeometryType.get(Polygon.class));

		constraints.add(new GeometryFactory(this));

		return constraints;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.AbstractGeometryHandler#initSupportedTypes()
	 */
	@Override
	protected Set<? extends QName> initSupportedTypes() {
		Set<QName> types = new HashSet<QName>();

		types.add(new QName(NS_GML, ENVELOPE_TYPE));
		types.add(new QName(NS_GML_32, ENVELOPE_TYPE));

		types.add(new QName(NS_GML, ENVELOPE_WITH_TIME_PERIOD_TYPE));
		types.add(new QName(NS_GML_32, ENVELOPE_WITH_TIME_PERIOD_TYPE));

		return types;
	}

}
