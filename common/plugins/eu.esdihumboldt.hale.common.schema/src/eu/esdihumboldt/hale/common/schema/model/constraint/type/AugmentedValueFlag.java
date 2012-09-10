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

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Flags if an augmented value is present for a type, meaning a value that is
 * not defined through the schema, but associated additionally to an instance.
 * Disabled by default. Should never be enabled when {@link HasValueFlag} is
 * enabled, meaning there is a value defined through the schema.<br>
 * <br>
 * The {@link Binding} constraint defines the Java binding for the value.
 * 
 * @see Binding
 * @see HasValueFlag
 * 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public class AugmentedValueFlag extends AbstractFlagConstraint implements TypeConstraint {

	/**
	 * Enabled augmented value flag
	 */
	public static final AugmentedValueFlag ENABLED = new AugmentedValueFlag(true);

	/**
	 * Disabled augmented value flag
	 */
	public static final AugmentedValueFlag DISABLED = new AugmentedValueFlag(false);

	/**
	 * Get the augmented value flag
	 * 
	 * @param augmentedValue if the flag shall be enabled
	 * @return the flag
	 */
	public static AugmentedValueFlag get(boolean augmentedValue) {
		return (augmentedValue) ? (ENABLED) : (DISABLED);
	}

	/**
	 * Creates a default augmented value flag, which is disabled. If possible,
	 * instead of creating an instance, use {@link #ENABLED} or
	 * {@link #DISABLED}.
	 * 
	 * @see Constraint
	 */
	public AugmentedValueFlag() {
		this(false);
	}

	/**
	 * @see AbstractFlagConstraint#AbstractFlagConstraint(boolean)
	 */
	private AugmentedValueFlag(boolean enabled) {
		super(enabled);
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
