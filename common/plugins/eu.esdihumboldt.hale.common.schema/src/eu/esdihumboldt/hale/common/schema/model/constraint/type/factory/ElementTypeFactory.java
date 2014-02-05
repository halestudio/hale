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

import java.text.MessageFormat;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueProperties;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ElementType;

/**
 * Converts {@link ElementType} constraints to {@link Value}s and vice versa.
 * 
 * @author Simon Templer
 */
public class ElementTypeFactory implements ValueConstraintFactory<ElementType> {

	/**
	 * Name of the property holding the type identifier/index.
	 */
	public static final String P_TYPE = "type";

	/**
	 * Name of the property holding the binding.
	 */
	public static final String P_BINDING = "binding";

	@Override
	public Value store(ElementType constraint, Map<TypeDefinition, String> typeIndex)
			throws Exception {
		ValueProperties props = new ValueProperties();

		// type definition
		if (constraint.getDefinition() != null) {
			String index = typeIndex.get(constraint.getDefinition());
			if (index != null) {
				props.put(P_TYPE, Value.of(index));
			}
		}

		// binding
		String className = constraint.getBinding().getName();
		props.put(P_BINDING, Value.of(className));

		return props.toValue();
	}

	@Override
	public ElementType restore(Value value, Definition<?> definition,
			Map<String, TypeDefinition> typeIndex, ClassResolver resolver) throws Exception {
		ValueProperties props = value.as(ValueProperties.class);

		// try type definition
		String index = props.getSafe(P_TYPE).as(String.class);
		if (index != null) {
			TypeDefinition def = typeIndex.get(index);
			if (def != null) {
				return ElementType.createFromType(def);
			}
		}

		// fall back to binding
		String binding = props.getSafe(P_BINDING).as(String.class);
		Class<?> clazz = resolver.loadClass(binding);
		if (clazz == null) {
			throw new IllegalStateException(MessageFormat.format(
					"Could not resolve class {0} for element type binding", binding));
		}

		return ElementType.get(clazz);
	}

}
