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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
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
 * Handler for polygon geometries
 * 
 * @author Patrick Lieb
 * @author Simon Templer
 */
public class PolygonHandler extends InterpolationSupportedGeometryHandler {

	private static final ALogger log = ALoggerFactory.getLogger(PolygonHandler.class);

	private static final String POLYGON_TYPE = "PolygonType";

	private static final String POLYGON_PATCH_TYPE = "PolygonPatchType";

	private static final String RECTANGLE_TYPE = "RectangleType";

	private static final String TRIANGLE_TYPE = "TriangleType";

	/**
	 * @see GeometryHandler#createGeometry(Instance, int, IOProvider)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object createGeometry(Instance instance, int srsDimension, IOProvider reader)
			throws GeometryNotSupportedException {

		LinearRing[] holes = null;
		Polygon polygon = null;

		CRSDefinition crs = null;

		// for use with GML 2
		// to parse outer linear rings
		Collection<Object> values = PropertyResolver.getValues(instance,
				"outerBoundaryIs.LinearRing", false);
		if (values != null && !values.isEmpty()) {
			Iterator<Object> iterator = values.iterator();
			List<LinearRing> outerRing = new ArrayList<>(1);
			while (iterator.hasNext()) {
				Object value = iterator.next();
				if (value instanceof Instance) {
					// outerRing must be a
					// GeometryProperty<LinearRing> instance
					GeometryProperty<LinearRing> ring = (GeometryProperty<LinearRing>) ((Instance) value)
							.getValue();
					outerRing.add(ring.getGeometry());
					crs = checkCommonCrs(crs, ring.getCRSDefinition());
				}
			}

			// to parse inner linear rings
			values = PropertyResolver.getValues(instance, "innerBoundaryIs.LinearRing", false);
			if (values != null && !values.isEmpty()) {
				iterator = values.iterator();
				List<LinearRing> innerRings = new ArrayList<LinearRing>();
				while (iterator.hasNext()) {
					Object value = iterator.next();
					if (value instanceof Instance) {
						// innerRings have to be a
						// GeometryProperty<LinearRing> instance
						GeometryProperty<LinearRing> ring = (GeometryProperty<LinearRing>) ((Instance) value)
								.getValue();
						innerRings.add(ring.getGeometry());
						crs = checkCommonCrs(crs, ring.getCRSDefinition());
					}
				}
				innerRings = moveLinerRingsToUniversalGrid(innerRings, reader);
				holes = innerRings.toArray(new LinearRing[innerRings.size()]);
			}
			outerRing = moveLinerRingsToUniversalGrid(outerRing, reader);
			polygon = getGeometryFactory().createPolygon(outerRing.get(0), holes);
		}

		// for use with GML 3, 3.1 and 3.2
		// to parse inner linear rings
		if (polygon == null) {
			values = PropertyResolver.getValues(instance, "interior.LinearRing", false);
			Collection<Object> ringValues = PropertyResolver.getValues(instance, "interior.Ring",
					false);
			values = combineCollections(values, ringValues);
			if (values != null && !values.isEmpty()) {
				Iterator<Object> iterator = values.iterator();
				List<LinearRing> innerRings = new ArrayList<LinearRing>();
				while (iterator.hasNext()) {
					Object value = iterator.next();
					if (value instanceof Instance) {
						// innerRings have to be a
						// GeometryProperty<LinearRing> instance
						GeometryProperty<LinearRing> ring = (GeometryProperty<LinearRing>) ((Instance) value)
								.getValue();
						innerRings.add(ring.getGeometry());
						crs = checkCommonCrs(crs, ring.getCRSDefinition());
					}
				}
				innerRings = moveLinerRingsToUniversalGrid(innerRings, reader);
				holes = innerRings.toArray(new LinearRing[innerRings.size()]);
			}

			// to parse outer linear rings
			values = PropertyResolver.getValues(instance, "exterior.LinearRing", false);
			ringValues = PropertyResolver.getValues(instance, "exterior.Ring", false);
			values = combineCollections(values, ringValues);
			List<LinearRing> outerRing = new ArrayList<>(1);
			if (values != null && !values.isEmpty()) {
				Iterator<Object> iterator = values.iterator();
				while (iterator.hasNext()) {
					Object value = iterator.next();
					if (value instanceof Instance) {
						// outerRing must be a
						// GeometryProperty<LinearRing> instance
						GeometryProperty<LinearRing> ring = (GeometryProperty<LinearRing>) ((Instance) value)
								.getValue();
						outerRing.add(ring.getGeometry());
						crs = checkCommonCrs(crs, ring.getCRSDefinition());
					}
				}
				outerRing = moveLinerRingsToUniversalGrid(outerRing, reader);
				polygon = getGeometryFactory().createPolygon(outerRing.get(0), holes);
			}
		}

		if (polygon != null) {
			if (crs == null) {
				crs = GMLGeometryUtil.findCRS(instance);
			}
			return new DefaultGeometryProperty<Polygon>(crs, polygon);
		}
		throw new GeometryNotSupportedException();
	}

	private Collection<Object> combineCollections(Collection<Object> values1,
			Collection<Object> values2) {
		if (values1 == null) {
			return values2;
		}
		if (values2 == null) {
			return values1;
		}

		Collection<Object> result = new ArrayList<>(values1.size() + values2.size());
		result.addAll(values1);
		result.addAll(values2);
		return result;
	}

	private CRSDefinition checkCommonCrs(CRSDefinition commonCrs, CRSDefinition newCrs) {
		if (commonCrs == null) {
			return newCrs;
		}
		if (newCrs == null) {
			// ignore new CRS not being set
			return commonCrs;
		}

		if (!commonCrs.equals(newCrs)) {
			log.error("Combining geometries with different spatial reference systems.");
		}

		return commonCrs;
	}

	private List<LinearRing> moveLinerRingsToUniversalGrid(List<LinearRing> linearRings,
			IOProvider reader) {
		getInterpolationRequiredParameter(reader);
		if (!isKeepOriginal()) {
			List<LinearRing> newRings = new ArrayList<LinearRing>();
			for (LinearRing ring : linearRings) {
				Coordinate[] newCoordinates = moveToUniversalGrid(ring.getCoordinates());
				LinearRing newRing = getGeometryFactory().createLinearRing(newCoordinates);
				newRings.add(newRing);
			}
			return newRings;
		}
		else
			return linearRings;
	}

	/**
	 * @see FixedConstraintsGeometryHandler#initConstraints()
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
	 * @see AbstractGeometryHandler#initSupportedTypes()
	 */
	@Override
	protected Set<? extends QName> initSupportedTypes() {
		Set<QName> types = new HashSet<QName>();

		types.add(new QName(NS_GML, POLYGON_TYPE));
		types.add(new QName(NS_GML_32, POLYGON_TYPE));

		types.add(new QName(NS_GML, POLYGON_PATCH_TYPE));
		types.add(new QName(NS_GML_32, POLYGON_PATCH_TYPE));

		types.add(new QName(NS_GML, RECTANGLE_TYPE));
		types.add(new QName(NS_GML_32, RECTANGLE_TYPE));

		types.add(new QName(NS_GML, TRIANGLE_TYPE));
		types.add(new QName(NS_GML_32, TRIANGLE_TYPE));

		return types;
	}
}
