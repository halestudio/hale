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

/**
 * Specifies the default value for a property.
 *
 * @author Kai Schwierczek
 */
@Constraint(mutable = false)
public class DefaultValue implements PropertyConstraint {
	private static final Object NO_DEFAULT = new Object();

	private final Object value;

	/**
	 * Constructor with no default value set.
	 */
	public DefaultValue() {
		value = NO_DEFAULT;
	}

	/**
	 * Constructor with the given default value.
	 *
	 * @param value the default value
	 */
	public DefaultValue(Object value) {
		this.value = value;
	}

	/**
	 * Returns the set default value. If no default value was set,
	 * <code>null</code> will be returned. Use {@link #isSet()} to
	 * differentiate between <code>null</code> as default value.
	 *
	 * @return the set default value, or <code>null</code> if none was set
	 */
	public Object getValue() {
		return value == NO_DEFAULT ? null : value;
	}

	/**
	 * Returns whether a default value is set or not.
	 *
	 * @return whether a default value is set or not
	 */
	public boolean isSet() {
		return value != NO_DEFAULT;
	}
}
