/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.schema.model.constraint.type.factory;

import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;

/**
 * Converts a {@link GeometryType} constraint to a {@link Value} and vice versa.
 * 
 * @author Simon Templer
 */
public class GeometryTypeFactory implements ValueConstraintFactory<GeometryType> {

	@Override
	public Value store(GeometryType constraint, Map<TypeDefinition, String> typeIndex)
			throws Exception {
		if (!constraint.isGeometry() || constraint.getBinding() == null) {
			// default: no geometry
			return null;
		}
		return Value.of(constraint.getBinding().getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public GeometryType restore(Value value, Definition<?> definition,
			Map<String, TypeDefinition> typeIndex, ClassResolver resolver) throws Exception {
		Class<?> binding = resolver.loadClass(value.as(String.class), "com.vividsolutions.jts");

		if (binding == null) {
			throw new IllegalStateException("Could not resolve geometry type "
					+ value.as(String.class));
		}

		return GeometryType.get((Class<? extends Geometry>) binding);
	}

}
