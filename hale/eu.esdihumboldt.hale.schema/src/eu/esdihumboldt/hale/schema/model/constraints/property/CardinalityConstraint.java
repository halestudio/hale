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
 * exactly once.
 * @author Simon Templer
 */
@Immutable
public final class CardinalityConstraint implements PropertyConstraint {

	/**
	 * Value for unrestricted {@link #maxOccurs}
	 */
	public static final long UNRESTRICTED = -1;
	
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
	 * {@link #maxOccurs} one.
	 */
	public CardinalityConstraint() {
		this(1, 1);
	}
	
	/**
	 * Create a cardinality constraint
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
