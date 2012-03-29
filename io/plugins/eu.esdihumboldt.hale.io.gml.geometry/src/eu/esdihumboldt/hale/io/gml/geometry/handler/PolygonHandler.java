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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.LinearRing;
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
 * Handler for polygon geometries
 * 
 * @author Patrick Lieb
 */
public class PolygonHandler extends FixedConstraintsGeometryHandler {

	private static final String POLYGON_TYPE = "PolygonType";

	private Polygon polygon = null;

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler#createGeometry(eu.esdihumboldt.hale.common.instance.model.Instance)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object createGeometry(Instance instance)
			throws GeometryNotSupportedException {

		// XXX need support for instances of Rings

		LinearRing[] holes = null;

		// for use with GML 2
		// to parse inner linear rings
		Collection<Object> values = PropertyResolver.getValues(instance,
				"innerBoundaryIs.LinearRing", false);
		if (values != null && !values.isEmpty()) {
			Iterator<Object> iterator = values.iterator();
			List<LinearRing> innerRings = new ArrayList<LinearRing>();
			while (iterator.hasNext()) {
				Object value = iterator.next();
				if (value instanceof Instance) {
					// innerRings have to be a
					// DefaultGeometryProperty<LinearRing> instances
					innerRings
							.add(((DefaultGeometryProperty<LinearRing>) ((Instance) value)
									.getValue()).getGeometry());
				}
			}
			holes = innerRings.toArray(new LinearRing[innerRings.size()]);

			// to parse outer linear rings
			values = PropertyResolver.getValues(instance,
					"outerBoundaryIs.LinearRing", false);
			LinearRing outerRing = null;
			if (values != null && !values.isEmpty()) {
				iterator = values.iterator();
				while (iterator.hasNext()) {
					Object value = iterator.next();
					if (value instanceof Instance) {
						// outerRing must be a
						// DefaultGeometryProperty<LinearRing> instance
						outerRing = ((DefaultGeometryProperty<LinearRing>) ((Instance) value)
								.getValue()).getGeometry();
					}
				}

			}
			polygon = getGeometryFactory().createPolygon(outerRing, holes);
		}

		// for use with GML 3, 3.1 and 3.2
		// to parse inner linear rings
		if (polygon == null) {
			values = PropertyResolver.getValues(instance,
					"interior.LinearRing", false);
			if (values != null && !values.isEmpty()) {
				Iterator<Object> iterator = values.iterator();
				List<LinearRing> innerRings = new ArrayList<LinearRing>();
				while (iterator.hasNext()) {
					Object value = iterator.next();
					if (value instanceof Instance) {
						// innerRings have to be a
						// DefaultGeometryProperty<LinearRing> instances
						innerRings
								.add(((DefaultGeometryProperty<LinearRing>) ((Instance) value)
										.getValue()).getGeometry());
					}
				}
				holes = innerRings.toArray(new LinearRing[innerRings.size()]);
			}

			// to parse outer linear rings
			values = PropertyResolver.getValues(instance,
					"exterior.LinearRing", false);
			LinearRing outerRing = null;
			if (values != null && !values.isEmpty()) {
				Iterator<Object> iterator = values.iterator();
				while (iterator.hasNext()) {
					Object value = iterator.next();
					if (value instanceof Instance) {
						// outerRing must be a
						// DefaultGeometryProperty<LinearRing> instance
						outerRing = ((DefaultGeometryProperty<LinearRing>) ((Instance) value)
								.getValue()).getGeometry();
					}
				}
			}
			polygon = getGeometryFactory().createPolygon(outerRing, holes);
		}

		if (polygon != null) {
			CRSDefinition crsDef = GMLGeometryUtil.findCRS(instance);
			return new DefaultGeometryProperty<Polygon>(crsDef, polygon);
		}
		throw new GeometryNotSupportedException();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler#initConstraints()
	 */
	@Override
	protected Collection<? extends TypeConstraint> initConstraints() {
		Collection<TypeConstraint> constraints = new ArrayList<TypeConstraint>(
				2);

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

		types.add(new QName(NS_GML, POLYGON_TYPE));
		types.add(new QName(NS_GML_32, POLYGON_TYPE));

		return types;
	}
}
