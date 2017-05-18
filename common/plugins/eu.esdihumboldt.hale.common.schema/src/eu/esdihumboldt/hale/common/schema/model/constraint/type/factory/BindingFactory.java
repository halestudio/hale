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

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeReferenceBuilder;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;

/**
 * Converts a {@link Binding} constraint to a {@link Value} object and vice
 * versa.
 * 
 * @author Simon Templer
 */
public class BindingFactory implements ValueConstraintFactory<Binding> {

	@Override
	public Value store(Binding constraint, TypeReferenceBuilder typeIndex) {
		Class<?> clazz = constraint.getBinding();
		if (clazz != null) {
			return Value.of(clazz.getName());
		}
		// OK to fall back to default
		return null;
	}

	@Override
	public Binding restore(Value value, Definition<?> definition, TypeResolver typeIndex,
			ClassResolver resolver) throws Exception {
		String className = value.as(String.class);

		Class<?> clazz = resolver.loadClass(className);

		return Binding.get(clazz);
	}

}
