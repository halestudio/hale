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

package eu.esdihumboldt.hale.io.jdbc.constraints;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Specifies whether a property is an auto increment property.
 * 
 * XXX It is questionable whether it is possible to really use this,
 * as it is not supported by all database types
 *
 * @author Kai Schwierczek
 */
@Constraint(mutable = false)
public class AutoIncrementFlag extends AbstractFlagConstraint implements PropertyConstraint {
	private static final AutoIncrementFlag ENABLED = new AutoIncrementFlag(true);
	private static final AutoIncrementFlag DISABLED = new AutoIncrementFlag(false);
	
	/**
	 * Get the auto increment flag.
	 * 
	 * @param isAutoIncrement if the flag shall be enabled
	 * @return the flag
	 */
	public static AutoIncrementFlag get(boolean isAutoIncrement) {
		return isAutoIncrement ? ENABLED : DISABLED;
	}

	/**
	 * Creates a default auto increment flag, which is <code>false</code>.
	 */
	public AutoIncrementFlag() {
		super(false);
	}

	/**
	 * Create a auto increment flag with the specified enabled state.
	 *
	 * @param enabled if the flag shall be enabled
	 */
	private AutoIncrementFlag(boolean enabled) {
		super(enabled);
	}
}
