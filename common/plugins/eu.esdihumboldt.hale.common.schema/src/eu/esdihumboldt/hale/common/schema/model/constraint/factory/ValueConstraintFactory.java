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

/**
 * Creates a {@link Value} representation from a constraint and vice versa.
 * Implementations must have a default constructor and may not hold any state.
 * 
 * @author Simon Templer
 * @param <T> the constraint type
 */
public interface ValueConstraintFactory<T> {

	/**
	 * Create a representation as {@link Value} of the given constraint.
	 * 
	 * @param constraint the constraint to store as {@link Value}
	 * @param refBuilder the reference builder that creates references for type
	 *            definitions that may be resolved
	 * @return the {@link Value} representation, may be <code>null</code>
	 * @throws Exception if the conversion to a value fails
	 */
	public Value store(T constraint, TypeReferenceBuilder refBuilder) throws Exception;

	/**
	 * Restore a constraint from its {@link Value} representation.
	 * 
	 * @param value the {@link Value} to recreate the constraint from
	 * @param definition the definition the constraint will be associated to
	 * @param typeResolver the type index that allows resolving references to
	 *            type definitions
	 * @param classResolver the resolver for reconstructing classes
	 * @return the restored constraint
	 * @throws Exception if the creation of the constraint fails
	 */
	public T restore(Value value, Definition<?> definition, TypeResolver typeResolver,
			ClassResolver classResolver) throws Exception;

}
