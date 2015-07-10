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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryNotSupportedException;
import eu.esdihumboldt.hale.io.gml.geometry.constraint.GeometryFactory;

/**
 * TODO Type description
 * 
 * @author Sameer Sheikh
 */
public class InterlisLineStringHandler extends FixedConstraintsGeometryHandler {

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler#createGeometry(eu.esdihumboldt.hale.common.instance.model.Instance,
	 *      int)
	 */
	@Override
	public Object createGeometry(Instance instance, int srsDimension)
			throws GeometryNotSupportedException {
		Collection<Object> values = PropertyResolver.getValues(instance, "COORD", false);
		// Collection<Object> valuesArc = PropertyResolver.getValues(instance,
		// "ARC", false);

		List<Coordinate> cs = new ArrayList<Coordinate>();
		if (values != null && !values.isEmpty()) {

			Iterator<Object> iter = values.iterator();

			while (iter.hasNext()) {

				Object value = iter.next();

				if (value instanceof Instance) {

					Instance val = (Instance) value;
					Double[] co = new Double[3];
					Coordinate x = new Coordinate();
					int im = 0;
					for (QName q : val.getPropertyNames()) {

						Object[] c = val.getProperty(q);
						for (int len = 0; len < c.length; len++)
							if (c[len] instanceof Double) {
								co[im] = (Double) c[len];
							}

						im++;
					}
					for (int i = 0; i < srsDimension; i++) {
						x.setOrdinate(i, co[i]);
					}
					cs.add(x);
				}
			}
		}

		LineString line1 = null;

		CRSDefinition crsDef = new CodeDefinition("EPSG:" + 21781, null);

		if (cs.size() == 0 || cs.size() >= 2) {
			line1 = getGeometryFactory().createLineString(cs.toArray(new Coordinate[cs.size()]));

			if (line1 != null)
				return new DefaultGeometryProperty<LineString>(crsDef, line1);
		}
		if (cs.size() == 1) {
			Point point = getGeometryFactory().createPoint(cs.get(0));
			return new DefaultGeometryProperty<Point>(crsDef, point);
		}
		throw new GeometryNotSupportedException();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler#initConstraints()
	 */
	@Override
	protected Collection<? extends TypeConstraint> initConstraints() {

		Collection<TypeConstraint> constraints = new ArrayList<TypeConstraint>();

		constraints.add(Binding.get(GeometryProperty.class));
		constraints.add(new GeometryFactory(this));

		return constraints;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.AbstractGeometryHandler#initSupportedTypes()
	 */
	@Override
	protected Set<? extends QName> initSupportedTypes() {

		Set<QName> types = new HashSet<QName>();
		types.add(new QName(INTERLIS_NAME, "POLYLINE"));

		return types;
	}
	@Override
	public boolean identifiesTypeByName() {
		return true;
	}

}
