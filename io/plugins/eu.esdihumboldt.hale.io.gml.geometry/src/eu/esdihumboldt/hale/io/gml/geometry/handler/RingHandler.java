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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LinearRing;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
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
 * Handler for ring geometries (parsed to {@link LinearRing}s).
 * 
 * @author Simon Templer
 */
public class RingHandler extends FixedConstraintsGeometryHandler {

	private static final String RING_TYPE = "RingType";

	private static final ALogger log = ALoggerFactory.getLogger(RingHandler.class);

	private static final GenericGeometryHandler genericHandler = new GenericGeometryHandler();

	/**
	 * @see GeometryHandler#createGeometry(Instance, int, IOProvider)
	 */
	@Override
	public Object createGeometry(Instance instance, int srsDimension, IOProvider reader)
			throws GeometryNotSupportedException {
		return createGeometry(instance, srsDimension, true, reader);
	}

	/**
	 * Create a {@link LinearRing} geometry from the given instance.
	 * 
	 * @param instance the instance
	 * @param srsDimension the SRS dimension
	 * @param allowTryOtherDimension if trying another dimension is allowed on
	 *            failure (e.g. 3D instead of 2D)
	 * @param reader the I/O Provider to get value
	 * @return the {@link LinearRing} geometry
	 * @throws GeometryNotSupportedException if the type definition doesn't
	 *             represent a geometry type supported by the handler
	 */
	protected GeometryProperty<LinearRing> createGeometry(Instance instance, int srsDimension,
			boolean allowTryOtherDimension, IOProvider reader)
					throws GeometryNotSupportedException {

		LinearRing ring = null;

		// for use with GML 2, 3, 3.1, 3.2
		// use generic geometry handler to read curveMembers as MultiLineString
		// or LineString
		Collection<GeometryProperty<?>> properties = genericHandler.createGeometry(instance,
				srsDimension, reader);
		if (properties != null) {
			if (properties.size() == 1) {
				// geometry could be combined
				GeometryProperty<?> prop = properties.iterator().next();

				try {
					ring = getGeometryFactory().createLinearRing(
							filterDuplicates(prop.getGeometry().getCoordinates()));
				} catch (IllegalArgumentException e) {
					if (allowTryOtherDimension) {
						// the error
						// "Points of LinearRing do not form a closed
						// linestring"
						// can be an expression of a wrong dimension being used
						// we try an alternative, to be sure (e.g. 3D instead of
						// 2D)
						int alternativeDimension = (srsDimension == 2) ? (3) : (2);
						GeometryProperty<LinearRing> geom = createGeometry(instance,
								alternativeDimension, false, reader);
						log.debug("Assuming geometry is " + alternativeDimension + "-dimensional.");
						return geom;
					}
					throw e;
				}

				if (ring != null) {
					CRSDefinition crsDef = prop.getCRSDefinition();
					if (crsDef == null) {
						GMLGeometryUtil.findCRS(instance);
					}
					return new DefaultGeometryProperty<LinearRing>(crsDef, ring);
				}
			}
			else {
				throw new GeometryNotSupportedException(
						"Ring components could not be combined to a geometry");
			}
		}

		throw new GeometryNotSupportedException();
	}

	/**
	 * Filter duplicate appearing directly after each other in a sequence of
	 * coordinates.
	 * 
	 * @param coordinates the sequence of coordinates
	 * @return the filtered sequence
	 */
	private Coordinate[] filterDuplicates(Coordinate[] coordinates) {
		if (coordinates == null) {
			return null;
		}

		Coordinate lastCoordinate = null;
		List<Coordinate> result = new ArrayList<>(coordinates.length);
		for (Coordinate coordinate : coordinates) {
			if (lastCoordinate == null || !lastCoordinate.equals3D(coordinate)) {
				result.add(coordinate);
				lastCoordinate = coordinate;
			}
		}

		return result.toArray(new Coordinate[result.size()]);
	}

	@Override
	protected Collection<? extends TypeConstraint> initConstraints() {
		Collection<TypeConstraint> constraints = new ArrayList<TypeConstraint>(2);

		constraints.add(Binding.get(GeometryProperty.class));
		constraints.add(GeometryType.get(LinearRing.class));

		constraints.add(new GeometryFactory(this));

		return constraints;
	}

	@Override
	protected Set<? extends QName> initSupportedTypes() {
		Set<QName> types = new HashSet<QName>();

		types.add(new QName(NS_GML, RING_TYPE));
		types.add(new QName(NS_GML_32, RING_TYPE));

		return types;
	}

}
