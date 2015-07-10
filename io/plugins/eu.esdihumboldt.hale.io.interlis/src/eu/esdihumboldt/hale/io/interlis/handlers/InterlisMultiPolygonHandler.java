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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
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
public class InterlisMultiPolygonHandler extends FixedConstraintsGeometryHandler {

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler#createGeometry(eu.esdihumboldt.hale.common.instance.model.Instance,
	 *      int)
	 */
	@Override
	public Object createGeometry(Instance instance, int srsDimension)
			throws GeometryNotSupportedException {

		Collection<Object> values = PropertyResolver.getValues(instance, "BOUNDARY", false);
		List<Polygon> poly = new ArrayList<Polygon>();
		List<LineString> lines = new ArrayList<LineString>();

		MultiPolygon mpoly = null;
		CRSDefinition crsDef = null;
		for (Object o : values) {
			Polygon p = null;
			if (o instanceof GeometryProperty<?>) {
				DefaultGeometryProperty<?> dgp = (DefaultGeometryProperty<?>) ((Instance) o)
						.getValue();
				if (dgp.getGeometry() instanceof Polygon) {
					p = (Polygon) dgp.getGeometry();
					if (p != null)
						poly.add(p);
				}
				else if (dgp.getGeometry() instanceof LineString) {
					LineString line = (LineString) dgp.getGeometry();
					lines.add(line);

				}
				crsDef = dgp.getCRSDefinition();
			}

		}
		if (!poly.isEmpty()) {
			mpoly = getGeometryFactory().createMultiPolygon(poly.toArray(new Polygon[poly.size()]));

			if (!mpoly.isEmpty())
				return new DefaultGeometryProperty<MultiPolygon>(crsDef, mpoly);
		}
		if (!lines.isEmpty()) {
			MultiLineString mls = getGeometryFactory().createMultiLineString(
					lines.toArray(new LineString[lines.size()]));
			return new DefaultGeometryProperty<MultiLineString>(crsDef, mls);
		}
		throw new GeometryNotSupportedException();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler#initConstraints()
	 */
	@Override
	protected Collection<? extends TypeConstraint> initConstraints() {
		Collection<TypeConstraint> constraints = new ArrayList<TypeConstraint>();
		constraints.add(new GeometryFactory(this));
		// constraints.add(AugmentedValueFlag.ENABLED);
		constraints.add(Binding.get(GeometryProperty.class));
		return constraints;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.AbstractGeometryHandler#initSupportedTypes()
	 */
	@Override
	protected Set<? extends QName> initSupportedTypes() {

		Set<QName> types = new HashSet<QName>();
		types.add(new QName(INTERLIS_NAME, "SURFACE"));
		return types;

	}
	
	@Override
	public boolean identifiesTypeByName() {
		return true;
	}
}
