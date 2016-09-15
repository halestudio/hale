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

package eu.esdihumboldt.hale.common.schema.model.constraint.property;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;

/**
 * Specifies the cardinality for a property, default is for a property to occur
 * exactly once.
 * 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public class Cardinality implements GroupPropertyConstraint, PropertyConstraint {

	/**
	 * Value for unrestricted {@link #maxOccurs}
	 */
	public static final long UNBOUNDED = -1;

	/**
	 * Cardinality constraint for properties that occur exactly once (one)
	 */
	public static final Cardinality CC_EXACTLY_ONCE = new Cardinality(1, 1);

	/**
	 * Cardinality constraint for properties that occur once or not at all (zero
	 * to one)
	 */
	public static final Cardinality CC_OPTIONAL = new Cardinality(0, 1);

	/**
	 * Cardinality constraint for properties that occur at least once (one to
	 * infinity)
	 */
	public static final Cardinality CC_AT_LEAST_ONCE = new Cardinality(1, UNBOUNDED);

	/**
	 * Cardinality constraint for properties that occur in any number (zero to
	 * infinity)
	 */
	public static final Cardinality CC_ANY_NUMBER = new Cardinality(0, UNBOUNDED);

	/**
	 * Get the cardinality constraint with the given occurrences
	 * 
	 * @param minOccurs the number of minimum occurrences of a property, may not
	 *            be negative
	 * @param maxOccurs the number of maximum occurrences of a property,
	 *            {@value #UNBOUNDED} for an infinite maximum occurrence
	 * @return the cardinality constraint
	 */
	public static Cardinality get(long minOccurs, long maxOccurs) {
		// use singletons if possible
		if (minOccurs == 0) {
			if (maxOccurs == 1) {
				return CC_OPTIONAL;
			}
			else if (maxOccurs == UNBOUNDED) {
				return CC_ANY_NUMBER;
			}
		}
		else if (minOccurs == 1) {
			if (maxOccurs == 1) {
				return CC_EXACTLY_ONCE;
			}
			else if (maxOccurs == UNBOUNDED) {
				return CC_AT_LEAST_ONCE;
			}
		}

		return new Cardinality(minOccurs, maxOccurs);
	}

	/**
	 * The number of minimum occurrences of a property
	 */
	private final long minOccurs;

	/**
	 * The number of maximum occurrences of a property
	 */
	private final long maxOccurs;

	/**
	 * Creates a default cardinality constraint with {@link #minOccurs} and
	 * {@link #maxOccurs} one.<br>
	 * <br>
	 * NOTE: Instead of using the constructor to create new instances please use
	 * {@link #get(long, long)} if possible.
	 */
	public Cardinality() {
		this(1, 1);
	}

	/**
	 * Create a cardinality constraint.<br>
	 * <br>
	 * NOTE: Instead of using the constructor to create new instances please use
	 * {@link #get(long, long)} if possible.
	 * 
	 * @param minOccurs the number of minimum occurrences of a property, may not
	 *            be negative
	 * @param maxOccurs the number of maximum occurrences of a property,
	 *            {@value #UNBOUNDED} for an infinite maximum occurrence
	 */
	private Cardinality(long minOccurs, long maxOccurs) {
		super();
		this.minOccurs = minOccurs;
		this.maxOccurs = maxOccurs;
	}

	/**
	 * Get the number of minimum occurrences of a property
	 * 
	 * @return the number of minimum occurrences of a property
	 */
	public long getMinOccurs() {
		return minOccurs;
	}

	/**
	 * Get the number of maximum occurrences of a property
	 * 
	 * @return the number of maximum occurrences of a property
	 */
	public long getMaxOccurs() {
		return maxOccurs;
	}

	/**
	 * @return if the property may occur more than once
	 */
	public boolean mayOccurMultipleTimes() {
		return getMaxOccurs() == UNBOUNDED || getMaxOccurs() > 1;
	}

	@Override
	public String toString() {
		return getMinOccurs() + ".." + ((getMaxOccurs() == UNBOUNDED) ? ('n') : (getMaxOccurs()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (maxOccurs ^ (maxOccurs >>> 32));
		result = prime * result + (int) (minOccurs ^ (minOccurs >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cardinality other = (Cardinality) obj;
		if (maxOccurs != other.maxOccurs)
			return false;
		if (minOccurs != other.minOccurs)
			return false;
		return true;
	}

}
