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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.LineString;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryNotSupportedException;
import eu.esdihumboldt.hale.io.gml.geometry.constraint.GeometryFactory;
import eu.esdihumboldt.hale.io.gml.geometry.handler.LineStringHandler;
import eu.esdihumboldt.util.geometry.interpolation.ArcInterpolation;
import eu.esdihumboldt.util.geometry.interpolation.Interpolation;

/**
 * Handler for Arc geometries
 * 
 * @author Arun
 */
public class ArcHandler extends LineStringHandler {

	private static final String ARC_TYPE = "ArcType";

	private static final ALogger log = ALoggerFactory.getLogger(ArcHandler.class);

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.handler.LineStringHandler#initSupportedTypes()
	 */
	@Override
	protected Set<? extends QName> initSupportedTypes() {
		Set<QName> types = new HashSet<QName>();

		types.add(new QName(NS_GML, ARC_TYPE));
		types.add(new QName(NS_GML_32, ARC_TYPE));

		return types;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.handler.LineStringHandler#createGeometry(eu.esdihumboldt.hale.common.instance.model.Instance,
	 *      int, IOProvider)
	 */
	@Override
	public Object createGeometry(Instance instance, int srsDimension, IOProvider reader)
			throws GeometryNotSupportedException {
		@SuppressWarnings("unchecked")
		DefaultGeometryProperty<LineString> lineStringGeomProperty = (DefaultGeometryProperty<LineString>) super.createGeometry(
				instance, srsDimension, reader);

		// get interpolation required parameter
		getInterpolationRequiredParameter(reader);

		Interpolation<LineString> interpolation = new ArcInterpolation(
				lineStringGeomProperty.getGeometry().getCoordinates(), getMaxPositionalError(),
				isKeepOriginal());
		LineString interpolatedArc = interpolation.interpolateRawGeometry();
		if (interpolatedArc == null) {
			log.error("Arc could be not interpolated to Linestring");
			return null;
		}
		return new DefaultGeometryProperty<LineString>(lineStringGeomProperty.getCRSDefinition(),
				interpolatedArc);
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
	 * @see eu.esdihumboldt.hale.io.gml.geometry.handler.LineStringHandler#isLineStringRelocationRequired()
	 */
	@Override
	protected boolean isLineStringRelocationRequired() {
		return false;
	}

}
