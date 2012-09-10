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

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;

/**
 * Specifies that a property should be unique.
 * 
 * @author Kai Schwierczek
 */
@Constraint(mutable = false)
public class Unique implements PropertyConstraint {

	private final String identifier;

	/**
	 * Creates a default unique object that is disabled.
	 */
	public Unique() {
		identifier = null;
	}

	/**
	 * Creates a unique flag with the given identifier.
	 * 
	 * @param identifier the unique identifier. Unique constraints with the same
	 *            identifier share a unique context. <code>null</code> means not
	 *            unique at all.
	 */
	public Unique(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Returns whether the unique constraint is enabled, the identifier is not
	 * null.
	 * 
	 * @return whether the unique constraint is enabled
	 */
	public boolean isEnabled() {
		return identifier != null;
	}

	/**
	 * Returns the unique identifer.
	 * 
	 * @return the unique identifer
	 */
	public String getIdentifier() {
		return identifier;
	}
}
