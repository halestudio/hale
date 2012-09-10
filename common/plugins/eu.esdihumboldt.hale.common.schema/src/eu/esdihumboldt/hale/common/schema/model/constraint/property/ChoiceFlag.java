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
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Flags if a property group is a choice, disabled by default
 * 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public class ChoiceFlag extends AbstractFlagConstraint implements GroupPropertyConstraint {

	/**
	 * Enabled choice flag
	 */
	public static final ChoiceFlag ENABLED = new ChoiceFlag(true);

	/**
	 * Disabled choice flag
	 */
	public static final ChoiceFlag DISABLED = new ChoiceFlag(false);

	/**
	 * Get the choice flag
	 * 
	 * @param isChoice if the flag shall be enabled
	 * @return the flag
	 */
	public static ChoiceFlag get(boolean isChoice) {
		return (isChoice) ? (ENABLED) : (DISABLED);
	}

	/**
	 * Creates a default choice flag, which is disabled. If possible, instead of
	 * creating an instance, use {@link #get(boolean)}, {@link #ENABLED} or
	 * {@link #DISABLED}.
	 * 
	 * @see Cardinality
	 */
	public ChoiceFlag() {
		this(false);
	}

	/**
	 * @see AbstractFlagConstraint#AbstractFlagConstraint(boolean)
	 */
	private ChoiceFlag(boolean enabled) {
		super(enabled);
	}
}
