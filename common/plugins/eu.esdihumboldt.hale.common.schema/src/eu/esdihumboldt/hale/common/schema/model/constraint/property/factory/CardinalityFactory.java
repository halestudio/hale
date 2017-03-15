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

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeReferenceBuilder;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;

/**
 * Converts a {@link Cardinality} constraint to a {@link Value} and back. The
 * constraint is represented as a simple string, with the minimum value
 * delimited from the maximum value by two dots, e.g. <code>0..1</code>. The
 * character <code>n</code> represents an unbounded maximum value.
 * 
 * @author Simon Templer
 */
public class CardinalityFactory implements ValueConstraintFactory<Cardinality> {

	@Override
	public Value store(Cardinality constraint, TypeReferenceBuilder typeIndex) {
		// convert to a simple from..to string
		StringBuilder builder = new StringBuilder();
		builder.append(constraint.getMinOccurs());
		builder.append("..");
		if (constraint.getMaxOccurs() == Cardinality.UNBOUNDED) {
			builder.append('n');
		}
		else {
			builder.append(constraint.getMaxOccurs());
		}

		return Value.of(builder.toString());
	}

	@Override
	public Cardinality restore(Value value, Definition<?> definition, TypeResolver typeIndex,
			ClassResolver resolver) {
		String str = value.as(String.class);

		if (str != null) {
			String[] parts = str.split("\\.\\.");
			long from;
			long to;
			if (parts.length == 2) {
				// min and max given
				from = Long.parseLong(parts[0]);
				if ("n".equalsIgnoreCase(parts[1])) {
					to = Cardinality.UNBOUNDED;
				}
				else {
					to = Long.parseLong(parts[1]);
				}
			}
			else if (parts.length == 1) {
				// treat as single number giving min and max
				from = to = Long.parseLong(parts[0]);
			}
			else {
				throw new IllegalStateException(
						"No proper value defining a cardinality constraint given: " + str);
			}

			return Cardinality.get(from, to);
		}

		throw new IllegalStateException("No value defining a cardinality constraint given");
	}

}
