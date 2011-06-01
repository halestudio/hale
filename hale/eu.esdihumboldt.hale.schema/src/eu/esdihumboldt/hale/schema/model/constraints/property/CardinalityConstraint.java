/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.schema.model.constraints.property;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.schema.model.Constraint;
import eu.esdihumboldt.hale.schema.model.PropertyConstraint;

/**
 * Specifies the cardinality for a property, default is for a property to occur
 * once or not at all.
 * @author Simon Templer
 */
@Immutable
public final class CardinalityConstraint implements PropertyConstraint {

	/**
	 * Value for unrestricted {@link #maxOccurs}
	 */
	public static final long UNRESTRICTED = -1;
	
	/**
	 * Cardinality constraint for properties that occur exactly once (one)
	 */
	public static final CardinalityConstraint CC_EXACTLY_ONCE = new CardinalityConstraint(1, 1);
	
	/**
	 * Cardinality constraint for properties that occur once or not at all (zero to one)
	 */
	public static final CardinalityConstraint CC_OPTIONAL = new CardinalityConstraint(0, 1);
	
	/**
	 * Cardinality constraint for properties that occur at least once (one to infinity)
	 */
	public static final CardinalityConstraint CC_AT_LEAST_ONCE = new CardinalityConstraint(1, UNRESTRICTED);
	
	/**
	 * Cardinality constraint for properties that occur in any number (zero to infinity)
	 */
	public static final CardinalityConstraint CC_ANY_NUMBER = new CardinalityConstraint(0, UNRESTRICTED);
	
	/**
	 * Get the cardinality constraint with the given occurences
	 * 
	 * @param minOccurs the number of minimum occurrences of a property, may not
	 *   be negative
	 * @param maxOccurs the number of maximum occurrences of a property,
	 *   {@value #UNRESTRICTED} for an infinite maximum occurrence
	 * @return the cardinality constraint
	 */
	public static CardinalityConstraint getCardinality(long minOccurs, long maxOccurs) {
		// use singletons if possible
		if (minOccurs == 0) {
			if (maxOccurs == 1) {
				return CC_OPTIONAL;
			}
			else if (maxOccurs == UNRESTRICTED) {
				return CC_ANY_NUMBER;
			}
		}
		else if (minOccurs == 1) {
			if (maxOccurs == 1) {
				return CC_EXACTLY_ONCE;
			}
			else if (maxOccurs == UNRESTRICTED) {
				return CC_AT_LEAST_ONCE;
			} 
		}
		
		return new CardinalityConstraint(minOccurs, maxOccurs);
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
	 * Creates a default cardinality constraint with {@link #minOccurs} zero and
	 * {@link #maxOccurs} one.<br>
	 * <br>
	 * NOTE: Instead of using the constructor to create new instances please use
	 * {@link #getCardinality(long, long)} if possible.
	 */
	public CardinalityConstraint() {
		this(0, 1);
	}
	
	/**
	 * Create a cardinality constraint.<br>
	 * <br>
	 * NOTE: Instead of using the constructor to create new instances please use
	 * {@link #getCardinality(long, long)} if possible.
	 * 
	 * @param minOccurs the number of minimum occurrences of a property, may not
	 *   be negative
	 * @param maxOccurs the number of maximum occurrences of a property,
	 *   {@value #UNRESTRICTED} for an infinite maximum occurrence
	 */
	private CardinalityConstraint(long minOccurs, long maxOccurs) {
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
	 * @see Constraint#isMutable()
	 */
	@Override
	public boolean isMutable() {
		return false;
	}

}
