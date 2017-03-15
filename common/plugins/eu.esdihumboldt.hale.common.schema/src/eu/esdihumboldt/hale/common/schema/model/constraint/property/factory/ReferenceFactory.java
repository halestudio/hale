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

package eu.esdihumboldt.hale.common.schema.model.constraint.property.factory;

import java.text.MessageFormat;
import java.util.Optional;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueList;
import eu.esdihumboldt.hale.common.core.io.ValueProperties;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeReferenceBuilder;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;

/**
 * Converts a {@link Reference} constraint to a {@link Value} and vice versa.
 * Constraints extending {@link Reference} that override
 * {@link Reference#extractId(Object)} should provide their own factories.
 * 
 * @author Simon Templer
 */
public class ReferenceFactory implements ValueConstraintFactory<Reference> {

	/**
	 * Name of the property specifying if the constraint represents a reference.
	 */
	public static final String P_IS_REF = "isReference";

	/**
	 * Name of the property specifying the referenced types.
	 */
	public static final String P_TYPES = "referencedTypes";

	/**
	 * Value for the referenced types property specifying that the types are
	 * unknown.
	 */
	public static final String V_TYPES_UNKNOWN = "unknown";

	@Override
	public Value store(Reference constraint, TypeReferenceBuilder typeIndex) {
		ValueProperties props = new ValueProperties();

		props.put(P_IS_REF, Value.of(constraint.isReference()));

		if (constraint.getReferencedTypes() == null) {
			// referenced types are unknown
			props.put(P_TYPES, Value.of(V_TYPES_UNKNOWN));
		}
		else {
			// store type list
			ValueList types = new ValueList();

			for (TypeDefinition type : constraint.getReferencedTypes()) {
				// add each type index
				Optional<Value> ref = typeIndex.createReference(type);
				if (ref.isPresent()) {
					types.add(ref.get());
				}
				else {
					throw new IllegalStateException(MessageFormat.format(
							"Type {0} could not be resolved in type index", type.getName()));
				}
			}

			props.put(P_TYPES, types.toValue());
		}

		return props.toValue();
	}

	@Override
	public Reference restore(Value value, Definition<?> definition, TypeResolver typeIndex,
			ClassResolver resolver) throws Exception {
		ValueProperties props = value.as(ValueProperties.class);

		Reference ref = new Reference(props.get(P_IS_REF).as(Boolean.class, false));

		Value types = props.get(P_TYPES);
		if (types.isComplex()) {
			ValueList list = types.as(ValueList.class);
			if (list != null) {
				for (Value entry : list) {
					Optional<TypeDefinition> type = typeIndex.resolve(entry);
					if (type.isPresent()) {
						ref.addReferencedType(type.get());
					}
					else {
						throw new IllegalStateException(
								"Could not resolve type definition for index " + entry);
					}
				}
			}
		}

		return ref;
	}

}
