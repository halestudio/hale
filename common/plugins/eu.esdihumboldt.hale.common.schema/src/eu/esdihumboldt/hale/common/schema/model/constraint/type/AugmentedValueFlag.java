/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
	protected AugmentedValueFlag(boolean enabled) {
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
