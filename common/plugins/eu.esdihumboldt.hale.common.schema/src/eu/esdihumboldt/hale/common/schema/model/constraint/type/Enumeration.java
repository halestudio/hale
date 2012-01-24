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

package eu.esdihumboldt.hale.common.schema.model.constraint.type;

import java.util.Collection;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;

/**
 * Constraint that holds allowed values for a type
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
	 *   collection is transferred to the constraint
	 * @param allowOthers if other values are allowed
	 */
	public Enumeration(Collection<? extends T> values, boolean allowOthers) {
		super();
		this.values = values;
		this.allowOthers = allowOthers;
	}

	/**
	 * @return the collection of allowed values, <code>null</code> there is no
	 *   such restriction
	 */
	public Collection<? extends T> getValues() {
		return values;
	}

	/**
	 * @return if other values than those returned by {@link #getValues()} are
	 *   allowed for the type, should be ignored if {@link #getValues()} returns
	 *   <code>null</code>
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
