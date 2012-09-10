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

/**
 * Abstract constraint that represents a flag that can be enabled or disabled
 * 
 * @author Simon Templer
 */
@Immutable
public abstract class AbstractFlagConstraint {

	private final boolean enabled;

	/**
	 * Create a flag that is enabled or disabled
	 * 
	 * @param enabled if the flag is enabled
	 */
	protected AbstractFlagConstraint(boolean enabled) {
		super();

		this.enabled = enabled;
	}

	/**
	 * States if the flag is enabled
	 * 
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

}
