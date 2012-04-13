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
import java.util.Set;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
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
 * Handler for linear ring geometries
 * 
 * @author Patrick Lieb
 */
public class LinearRingHandler extends FixedConstraintsGeometryHandler {

	private static final String LINEAR_RING_TYPE = "LinearRingType";

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler#createGeometry(eu.esdihumboldt.hale.common.instance.model.Instance)
	 */
	@Override
	public Object createGeometry(Instance instance)
			throws GeometryNotSupportedException {

		LinearRing ring = null;
		LineStringHandler handler = new LineStringHandler();

		// for use with GML 2, 3, 3.1, 3.2
		@SuppressWarnings("unchecked")
		DefaultGeometryProperty<LineString> linestring = (DefaultGeometryProperty<LineString>) handler
				.createGeometry(instance);
		ring = getGeometryFactory().createLinearRing(
				linestring.getGeometry().getCoordinates());

		if (ring != null) {
			CRSDefinition crsDef = GMLGeometryUtil.findCRS(instance);
			return new DefaultGeometryProperty<LinearRing>(crsDef, ring);
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
		constraints.add(GeometryType.get(LinearRing.class));

		constraints.add(new GeometryFactory(this));

		return constraints;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.AbstractGeometryHandler#initSupportedTypes()
	 */
	@Override
	protected Set<? extends QName> initSupportedTypes() {
		Set<QName> types = new HashSet<QName>();

		types.add(new QName(NS_GML, LINEAR_RING_TYPE));
		types.add(new QName(NS_GML_32, LINEAR_RING_TYPE));

		return types;
	}

}
