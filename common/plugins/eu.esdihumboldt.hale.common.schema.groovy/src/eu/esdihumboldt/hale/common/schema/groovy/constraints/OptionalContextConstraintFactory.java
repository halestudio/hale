/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.common.schema.groovy.constraints;

import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder;
import eu.esdihumboldt.hale.common.schema.model.Constraint;

/**
 * Abstract class for constraint factories that don't rely on a context.
 * 
 * @see SchemaBuilder
 * @see Constraint
 * 
 * @param <T> the constraint type
 * @author Simon Templer
 */
public abstract class OptionalContextConstraintFactory<T> implements ConstraintFactory<T> {

	/**
	 * Create a constraint from the given argument.
	 * 
	 * @param arg the argument describing the constraint
	 * @return the constraint
	 */
	public T createConstraint(Object arg) {
		return createConstraint(arg, null);
	}
}
