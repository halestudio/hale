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

import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Unique;

/**
 * Converts {@link Unique} constraints to {@link Value} objects and vice versa.
 * 
 * @author Simon Templer
 */
public class UniqueConstraintFactory implements ValueConstraintFactory<Unique> {

	@Override
	public Value store(Unique constraint, Map<TypeDefinition, String> typeIndex) {
		if (constraint.isEnabled()) {
			return Value.of(constraint.getIdentifier());
		}
		// OK to fall back to default
		return null;
	}

	@Override
	public Unique restore(Value value, Definition<?> definition,
			Map<String, TypeDefinition> typeIndex, ClassResolver resolver) throws Exception {
		String context = value.as(String.class);
		return new Unique(context);
	}

}
