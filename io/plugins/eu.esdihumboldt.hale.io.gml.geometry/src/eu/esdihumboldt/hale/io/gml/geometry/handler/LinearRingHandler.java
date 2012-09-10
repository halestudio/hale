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

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
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
 * Handler for linear ring geometries
 * 
 * @author Patrick Lieb
 */
public class LinearRingHandler extends FixedConstraintsGeometryHandler {

	private static final String LINEAR_RING_TYPE = "LinearRingType";

	private static final ALogger log = ALoggerFactory.getLogger(LinearRingHandler.class);

	/**
	 * @see GeometryHandler#createGeometry(Instance, int)
	 */
	@Override
	public Object createGeometry(Instance instance, int srsDimension)
			throws GeometryNotSupportedException {
		return createGeometry(instance, srsDimension, true);
	}

	/**
	 * Create a {@link LinearRing} geometry from the given instance.
	 * 
	 * @param instance the instance
	 * @param srsDimension the SRS dimension
	 * @param allowTryOtherDimension if trying another dimension is allowed on
	 *            failure (e.g. 3D instead of 2D)
	 * @return the {@link LinearRing} geometry
	 * @throws GeometryNotSupportedException if the type definition doesn't
	 *             represent a geometry type supported by the handler
	 */
	protected GeometryProperty<LinearRing> createGeometry(Instance instance, int srsDimension,
			boolean allowTryOtherDimension) throws GeometryNotSupportedException {

		LinearRing ring = null;
		LineStringHandler handler = new LineStringHandler();

		// for use with GML 2, 3, 3.1, 3.2
		@SuppressWarnings("unchecked")
		DefaultGeometryProperty<LineString> linestring = (DefaultGeometryProperty<LineString>) handler
				.createGeometry(instance, srsDimension);
		try {
			ring = getGeometryFactory().createLinearRing(linestring.getGeometry().getCoordinates());
		} catch (IllegalArgumentException e) {
			if (allowTryOtherDimension) {
				// the error
				// "Points of LinearRing do not form a closed linestring"
				// can be an expression of a wrong dimension being used
				// we try an alternative, to be sure (e.g. 3D instead of 2D)
				int alternativeDimension = (srsDimension == 2) ? (3) : (2);
				GeometryProperty<LinearRing> geom = createGeometry(instance, alternativeDimension,
						false);
				log.debug("Assuming geometry is " + alternativeDimension + "-dimensional.");
				return geom;
			}
			throw e;
		}

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
		Collection<TypeConstraint> constraints = new ArrayList<TypeConstraint>(2);

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
