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

package eu.esdihumboldt.hale.common.schema.model.constraint.property;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Flags if a property is nillable, disabled by default
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public class NillableFlag extends AbstractFlagConstraint implements PropertyConstraint {

	/**
	 * Enabled nillable flag
	 */
	public static final NillableFlag ENABLED = new NillableFlag(true);
	
	/**
	 * Disabled nillable flag
	 */
	public static final NillableFlag DISABLED = new NillableFlag(false);
	
	/**
	 * Get the nillable flag
	 * 
	 * @param isNillable if the flag shall be enabled
	 * @return the flag
	 */
	public static NillableFlag get(boolean isNillable) {
		return (isNillable)?(ENABLED):(DISABLED);
	}
	
	/**
	 * Creates a default nillable flag, which is disabled. If possible, instead
	 * of creating an instance, use {@link #get(boolean)}, {@link #ENABLED} or 
	 * {@link #DISABLED}.
	 * 
	 * @see Cardinality
	 */
	public NillableFlag() {
		this(false);
	}
	
	/**
	 * @see AbstractFlagConstraint#AbstractFlagConstraint(boolean)
	 */
	private NillableFlag(boolean enabled) {
		super(enabled);
	}
	
}
