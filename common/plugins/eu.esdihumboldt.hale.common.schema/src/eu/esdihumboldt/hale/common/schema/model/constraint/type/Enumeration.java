/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.schema.model.constraint.type;

import java.util.Collection;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;

/**
 * Constraint that holds allowed values for a type
 * 
 * @param <T> the value type
 * 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public class Enumeration<T> implements TypeConstraint {

	private final Collection<? extends T> values;

	private final boolean allowOthers;

	/**
	 * Creates a default constraint where no restriction on the allowed values
	 * is present.
	 */
	public Enumeration() {
		super();
		values = null;
		allowOthers = true;
	}

	/**
	 * Create a constraint that holds allowed values for a type
	 * 
	 * @param values the collection of allowed values, ownership of the
	 *            collection is transferred to the constraint
	 * @param allowOthers if other values are allowed
	 */
	public Enumeration(Collection<? extends T> values, boolean allowOthers) {
		super();
		this.values = values;
		this.allowOthers = allowOthers;
	}

	/**
	 * @return the collection of allowed values, <code>null</code> there is no
	 *         such restriction
	 */
	public Collection<? extends T> getValues() {
		return values;
	}

	/**
	 * @return if other values than those returned by {@link #getValues()} are
	 *         allowed for the type, should be ignored if {@link #getValues()}
	 *         returns <code>null</code>
	 */
	public boolean isAllowOthers() {
		return allowOthers;
	}

	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// inherit unless overridden
		return true;
	}

}
