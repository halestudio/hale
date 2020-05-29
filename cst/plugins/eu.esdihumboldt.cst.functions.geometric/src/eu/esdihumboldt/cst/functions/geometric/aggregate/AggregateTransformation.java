/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.geometric.aggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.TransformationMessageImpl;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.GeometryFinder;
import eu.esdihumboldt.hale.common.instance.helper.DepthFirstInstanceTraverser;
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraverser;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.util.geometry.CurveHelper;

/**
 * Aggregates input geometries if possible.
 * 
 * @author Simon Templer
 */
public class AggregateTransformation extends
		AbstractSingleTargetPropertyTransformation<TransformationEngine> implements
		AggregateFunction {

	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {

		Iterable<Object> geometries = Iterables.transform(variables.get(null),
				new Function<PropertyValue, Object>() {

					@Override
					public Object apply(PropertyValue input) {
						return input.getValue();
					}
				});
		return aggregateGeometries(geometries, log, getCell());
	}

	/**
	 * Aggregates geometries contained in the provided objects.
	 * 
	 * @param geometries the geometries or instances containing geometries
	 * @param cell the currently process cell or <code>null</code>
	 * @param log the transformation log or <code>null</code>
	 * @return the aggregated geometry
	 * @throws TransformationException if source geometries don't have a common
	 *             CRS
	 * @throws NoResultException if the result extent would be <code>null</code>
	 */
	public static GeometryProperty<?> aggregateGeometries(Iterable<?> geometries,
			@Nullable TransformationLog log, @Nullable Cell cell) throws NoResultException,
			TransformationException {
		InstanceTraverser traverser = new DepthFirstInstanceTraverser(true);
		GeometryFinder geoFind = new GeometryFinder(null);

		CRSDefinition commonCrs = null;
		Class<? extends Geometry> commonGeometryType = null;

		List<Geometry> collectedGeometries = new ArrayList<>();

		for (Object value : geometries) {

			// find contained geometries
			traverser.traverse(value, geoFind);

			for (GeometryProperty<?> geom : geoFind.getGeometries()) {
				// check CRS
				// no CRS or one common CRS is OK
				if (commonCrs == null) {
					commonCrs = geom.getCRSDefinition();
				}
				else {
					if (geom.getCRSDefinition() != null
							&& !geom.getCRSDefinition().equals(commonCrs)) {
						// CRS doesn't match
						throw new TransformationException(
								"Source geometries don't have a common CRS.");
					}
				}

				Geometry g = geom.getGeometry();

				// determine common geometry type: point / line / polygon
				if (commonGeometryType == null) {
					commonGeometryType = getContainedGeometryType(g.getClass());
				}
				else {
					Class<? extends Geometry> currentType = getContainedGeometryType(g.getClass());
					if (!commonGeometryType.isAssignableFrom(currentType)) {
						if (currentType.isAssignableFrom(commonGeometryType)) {
							commonGeometryType = currentType;
						}
						else {
							commonGeometryType = Geometry.class;
						}
					}
				}

				// collect geometry
				for (int i = 0; i < g.getNumGeometries(); i++) {
					collectedGeometries.add(g.getGeometryN(i));
				}
			}

			geoFind.reset();
		}

		if (commonGeometryType != null && commonGeometryType.equals(Geometry.class)) {
			if (log != null && cell != null) {
				log.warn(new TransformationMessageImpl(cell,
						"Could not find common geometry type for aggregation", null));
			}
		}

		if (commonGeometryType != null) {
			Geometry combined = combineGeometries(collectedGeometries, commonGeometryType);
			return new DefaultGeometryProperty<Geometry>(commonCrs, combined);
		}
		throw new NoResultException();
	}

	@SuppressWarnings("unchecked")
	private static Geometry combineGeometries(List<? extends Geometry> collectedGeometries,
			Class<? extends Geometry> commonGeometryType) throws ClassCastException {
		GeometryFactory fact = new GeometryFactory();

		if (Point.class.isAssignableFrom(commonGeometryType)) {
			return fact.createMultiPoint(((Collection<Point>) collectedGeometries)
					.toArray(new Point[collectedGeometries.size()]));
		}
		else if (LineString.class.isAssignableFrom(commonGeometryType)) {
			return CurveHelper.combineCurve((List<LineString>) collectedGeometries, fact, false);
		}
		else if (Polygon.class.isAssignableFrom(commonGeometryType)) {
			return fact.createMultiPolygon(((Collection<Polygon>) collectedGeometries)
					.toArray(new Polygon[collectedGeometries.size()]));
		}
		else {
			return fact.createGeometryCollection(collectedGeometries
					.toArray(new Geometry[collectedGeometries.size()]));
		}
	}

	/**
	 * Determine the contained geometry type from a geometry.
	 * 
	 * @param clazz the geometry class
	 * @return the contained geometry typed
	 */
	private static Class<? extends Geometry> getContainedGeometryType(
			Class<? extends Geometry> clazz) {
		if (GeometryCollection.class.isAssignableFrom(clazz)) {
			if (MultiLineString.class.isAssignableFrom(clazz)) {
				return LineString.class;
			}
			if (MultiPoint.class.isAssignableFrom(clazz)) {
				return Point.class;
			}
			if (MultiPolygon.class.isAssignableFrom(clazz)) {
				return Polygon.class;
			}
		}
		else {
			if (LineString.class.isAssignableFrom(clazz)) {
				return LineString.class;
			}
			if (Point.class.isAssignableFrom(clazz)) {
				return Point.class;
			}
			if (Polygon.class.isAssignableFrom(clazz)) {
				return Polygon.class;
			}
		}

		return Geometry.class;
	}

}
