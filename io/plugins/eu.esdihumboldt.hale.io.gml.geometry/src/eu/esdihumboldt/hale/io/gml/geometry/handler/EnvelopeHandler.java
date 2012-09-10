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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
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
 * Handler for envelope geometries
 * 
 * @author Patrick Lieb
 */
public class EnvelopeHandler extends FixedConstraintsGeometryHandler {

	private static final String ENVELOPE_TYPE = "EnvelopeType";

	private static final String ENVELOPE_WITH_TIME_PERIOD_TYPE = "EnvelopeWithTimePeriodType";

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler#createGeometry(eu.esdihumboldt.hale.common.instance.model.Instance,
	 *      int)
	 */
	@Override
	public Object createGeometry(Instance instance, int srsDimension)
			throws GeometryNotSupportedException {

		MultiPoint envelope;
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

		Coordinate[] coordinates = new Coordinate[] { points.get(0).getCoordinate(),
				points.get(1).getCoordinate() };
		envelope = getGeometryFactory().createMultiPoint(coordinates);

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
