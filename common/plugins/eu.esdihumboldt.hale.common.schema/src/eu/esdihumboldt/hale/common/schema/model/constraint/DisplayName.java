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

package eu.esdihumboldt.hale.common.schema.model.constraint;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;

/**
 * Constraint that can provide a custom display name. By default has no custom
 * name.
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public class DisplayName implements GroupPropertyConstraint,
		PropertyConstraint, TypeConstraint {

	private final String customName;

	/**
	 * Create a default constraint that has no custom name.  
	 */
	public DisplayName() {
		this(null);
	}

	/**
	 * Create a custom display name constraint
	 * 
	 * @param customName the custom display name
	 */
	public DisplayName(String customName) {
		super();
		this.customName = customName;
	}

	/**
	 * Get the custom display name.
	 * @return the customName the custom display name or <code>null</code> if
	 *   there is no custom name
	 */
	public String getCustomName() {
		return customName;
	}

	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// no inheritance for display names
		return false;
	}
	
}
