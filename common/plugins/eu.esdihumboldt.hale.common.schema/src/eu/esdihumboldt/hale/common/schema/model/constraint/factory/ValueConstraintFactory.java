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
	 * @return the {@link Value} representation
	 */
	public Value store(T constraint);

	/**
	 * Restore a constraint from its {@link Value} representation.
	 * 
	 * @param value the {@link Value} to recreate the constraint from
	 * @return the restored constraint
	 * @throws Exception if the creation of the constraint fails
	 */
	public T restore(Value value) throws Exception;

}
