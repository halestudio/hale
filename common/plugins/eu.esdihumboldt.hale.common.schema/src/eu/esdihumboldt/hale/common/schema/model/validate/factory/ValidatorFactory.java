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

package eu.esdihumboldt.hale.common.schema.model.validate.factory;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.util.validator.Validator;

/**
 * Creates a {@link Value} representation from a {@link Validator} and vice
 * versa. Implementations must have a default constructor and may not hold any
 * state.
 * 
 * @author Simon Templer
 * @param <T> the validator type
 */
public interface ValidatorFactory<T extends Validator> {

	/**
	 * Create a representation as {@link Value} of the given validator.
	 * 
	 * @param validator the validator to store as {@link Value}
	 * @return the {@link Value} representation, may be <code>null</code>
	 * @throws Exception if the validator cannot be represented as value
	 */
	public Value store(T validator) throws Exception;

	/**
	 * Restore a constraint from its {@link Value} representation.
	 * 
	 * @param value the {@link Value} to recreate the validator from
	 * @return the restored validator
	 * @throws Exception if the creation of the validator fails
	 */
	public Validator restore(Value value) throws Exception;

}
