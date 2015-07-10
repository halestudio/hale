/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.interlis.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryNotSupportedException;
import eu.esdihumboldt.hale.io.gml.geometry.constraint.GeometryFactory;

/**
 * TODO Type description
 * 
 * @author Sameer Sheikh
 */
public class InterlisPolygonHandler extends FixedConstraintsGeometryHandler {

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler#createGeometry(eu.esdihumboldt.hale.common.instance.model.Instance,
	 *      int)
	 */
	@Override
	public Object createGeometry(Instance instance, int srsDimension)
			throws GeometryNotSupportedException {

	
		Collection<Object> values = PropertyResolver.getValues(instance, "POLYLINE");

		List<Coordinate> coords = new ArrayList<Coordinate>();
		CRSDefinition crsDef = null;

		for (Object o : values) {

			if (o instanceof GeometryProperty<?>) {
				GeometryProperty<?> ring = (GeometryProperty<?>) o;

				if (ring.getGeometry() instanceof LineString) {

					LineString line = (LineString) ring.getGeometry();
					coords.addAll(Arrays.asList(line.getCoordinates()));
				}
				else if (ring.getGeometry() instanceof Point) {
					Point point = (Point) ring.getGeometry();
					coords.addAll(Arrays.asList(point.getCoordinates()));
				}
				crsDef = ring.getCRSDefinition();
			}

		}
		// For creating a polygon atleast 4 co ordinates are required.
		// If geometry is not a closed ring, it wont create a polygon out of it
		// make it a close ring manually if first and the last coordinate do not
		// match, add first coordinate as a last coordinate.

		if (coords.size() >= 4) {

			Coordinate first = coords.get(0);
			Coordinate last = coords.get(coords.size() - 1);

			if (!(first.x == last.x && first.y == last.y)) {
				// coords.add(first);

			}

			Polygon pol = getGeometryFactory().createPolygon(
					coords.toArray(new Coordinate[coords.size()]));

			if (pol != null)
				return new DefaultGeometryProperty<Polygon>(crsDef, pol);
		}

		throw new GeometryNotSupportedException();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler#initConstraints()
	 */
	@Override
	protected Collection<? extends TypeConstraint> initConstraints() {
		Collection<TypeConstraint> types = new ArrayList<TypeConstraint>();
		types.add(new GeometryFactory(this));
		types.add(Binding.get(GeometryProperty.class));
		return types;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.AbstractGeometryHandler#initSupportedTypes()
	 */
	@Override
	protected Set<? extends QName> initSupportedTypes() {
		Set<QName> types = new HashSet<QName>();
		types.add(new QName(INTERLIS_NAME, "BOUNDARY"));
		return types;
	}
	
	@Override
	public boolean identifiesTypeByName() {
	return true;
	}
}
