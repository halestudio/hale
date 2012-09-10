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

package eu.esdihumboldt.hale.io.xsd.constraint;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Flags if a type's super type relation is a restriction, disabled by default
 * 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public final class RestrictionFlag extends AbstractFlagConstraint implements TypeConstraint {

	/**
	 * Enabled restriction flag
	 */
	public static final RestrictionFlag ENABLED = new RestrictionFlag(true);

	/**
	 * Disabled restriction flag
	 */
	public static final RestrictionFlag DISABLED = new RestrictionFlag(false);

	/**
	 * Creates a default restriction flag, which is disabled. If possible,
	 * instead of creating an instance, use {@link #ENABLED} or
	 * {@link #DISABLED}.
	 * 
	 * @see Constraint
	 */
	public RestrictionFlag() {
		this(false);
	}

	/**
	 * @see AbstractFlagConstraint#AbstractFlagConstraint(boolean)
	 */
	private RestrictionFlag(boolean enabled) {
		super(enabled);
	}

	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// must be set explicitly
		return false;
	}

}
