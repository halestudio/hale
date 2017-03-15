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

package eu.esdihumboldt.hale.common.schema.model.constraint.factory;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Base class for flag value constraint factories.
 * 
 * @author Simon Templer
 * @param <T> the concrete flag constraint type
 */
public abstract class FlagConstraintFactory<T extends AbstractFlagConstraint>
		implements ValueConstraintFactory<T> {

	@Override
	public Value store(T constraint, TypeReferenceBuilder typeIndex) {
		return Value.of(constraint.isEnabled());
	}

	@Override
	public T restore(Value value, Definition<?> definition, TypeResolver typeIndex,
			ClassResolver resolver) throws Exception {
		return restore(value.asType(Boolean.class));
	}

	/**
	 * Restore the flag constraint.
	 * 
	 * @param enabled if the flag should be enabled
	 * @return the flag constraint
	 */
	protected abstract T restore(boolean enabled);

}
