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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.io.gml.geometry.AbstractGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.GMLGeometryUtil;
import eu.esdihumboldt.hale.io.gml.geometry.constraint.GeometryFactory;
import eu.esdihumboldt.util.geometry.CurveHelper;

/**
 * Handler for building curve geometries.
 * 
 * @author Simon Templer
 */
public class CurveHandler extends GenericGeometryHandler {

	private static final String CURVE_TYPE = "CurveType";

	private static final String COMPOSITE_CURVE_TYPE = "CompositeCurveType";

	private static final String ORIENTABLE_CURVE_TYPE = "OrientableCurveType";

	private static final ALogger log = ALoggerFactory.getLogger(CurveHandler.class);

	/**
	 * @see FixedConstraintsGeometryHandler#initConstraints()
	 */
	@Override
	protected Collection<? extends TypeConstraint> initConstraints() {
		Collection<TypeConstraint> constraints = new ArrayList<TypeConstraint>(2);

		constraints.add(Binding.get(GeometryProperty.class));
		// FIXME can also be a MultiLineString - is this a problem?
		constraints.add(GeometryType.get(LineString.class));

		constraints.add(new GeometryFactory(this));

		return constraints;
	}

	/**
	 * @see AbstractGeometryHandler#initSupportedTypes()
	 */
	@Override
	protected Set<? extends QName> initSupportedTypes() {
		Set<QName> types = new HashSet<QName>();

		types.add(new QName(NS_GML, CURVE_TYPE));
		types.add(new QName(NS_GML_32, CURVE_TYPE));

		types.add(new QName(NS_GML, COMPOSITE_CURVE_TYPE));
		types.add(new QName(NS_GML_32, COMPOSITE_CURVE_TYPE));

		types.add(new QName(NS_GML, ORIENTABLE_CURVE_TYPE));
		types.add(new QName(NS_GML_32, ORIENTABLE_CURVE_TYPE));

		return types;
	}

	@Override
	protected Geometry combine(LineString[] lineStrings, IOProvider reader) {
		if (lineStrings != null && lineStrings.length == 1) {
			return lineStrings[0];
		}

		if (GMLGeometryUtil.isCombineCompositesEnabled(reader)
				&& GMLGeometryUtil.is2D(lineStrings)) {
			/*
			 * XXX Third dimension curves are not supported, because CurveHelper
			 * uses Coordinate.equals(...) which does only a 2D check.
			 */

			try {
				LineString combined = CurveHelper.combineCurve(Arrays.asList(lineStrings),
						getGeometryFactory());
				if (combined != null) {
					return combined;
				}
				else {
					log.debug("Curve elements could not be combined to single LineString");
				}
			} catch (Exception e) {
				// ignore
				log.debug("Error trying to combine curve", e);
			}
		}

		// fall-back
		return super.combine(lineStrings, reader);
	}

}
