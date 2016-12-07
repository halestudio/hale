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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.cst.functions.geometric.extent.ExtentTransformation;
import eu.esdihumboldt.cst.functions.geometric.extent.ExtentType;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.io.gml.geometry.AbstractGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.GMLGeometryUtil;
import eu.esdihumboldt.hale.io.gml.geometry.constraint.GeometryFactory;

/**
 * Handler for building surface geometries.
 * 
 * @author Simon Templer
 */
public class SurfaceHandler extends GenericGeometryHandler {

	private static final String SURFACE_TYPE = "SurfaceType";

	private static final String COMPOSITE_SURFACE_TYPE = "CompositeSurfaceType";

	private static final String ORIENTABLE_SURFACE_TYPE = "OrientableSurfaceType";

	private static final ALogger log = ALoggerFactory.getLogger(SurfaceHandler.class);

	/**
	 * @see FixedConstraintsGeometryHandler#initConstraints()
	 */
	@Override
	protected Collection<? extends TypeConstraint> initConstraints() {
		Collection<TypeConstraint> constraints = new ArrayList<TypeConstraint>(2);

		constraints.add(Binding.get(GeometryProperty.class));
		// FIXME can also be a MultiPolygon - is this a problem?
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

		types.add(new QName(NS_GML, SURFACE_TYPE));
		types.add(new QName(NS_GML_32, SURFACE_TYPE));

		types.add(new QName(NS_GML, COMPOSITE_SURFACE_TYPE));
		types.add(new QName(NS_GML_32, COMPOSITE_SURFACE_TYPE));

		types.add(new QName(NS_GML, ORIENTABLE_SURFACE_TYPE));
		types.add(new QName(NS_GML_32, ORIENTABLE_SURFACE_TYPE));

		return types;
	}

	@Override
	protected Geometry combine(Polygon[] polygons, IOProvider reader) {
		if (polygons != null && polygons.length == 1) {
			return polygons[0];
		}

		if (GMLGeometryUtil.isCombineCompositesEnabled(reader) && GMLGeometryUtil.is2D(polygons)) {
			/*
			 * It was found that the UNION calculation as below seems to ignore
			 * the third dimension. So only handle 2D polygons.
			 */

			try {
				GeometryProperty<?> prop = ExtentTransformation
						.calculateExtent(Arrays.asList(polygons), ExtentType.UNION);
				if (prop != null) {
					Geometry geom = prop.getGeometry();
					if (geom instanceof Polygon) {
						return geom;
					}
					else {
						/*
						 * Probably a MultiPolygon, for instance for polygons
						 * that only touch each other in one point or for 3D
						 * polygons.
						 */
						log.debug("Could not combine surface to single polygon");
					}
				}
			} catch (NoResultException | TransformationException e) {
				// ignore
				log.debug("Could not combine surface to single polygon");
			}
		}

		// fall-back
		return super.combine(polygons, reader);
	}

}
