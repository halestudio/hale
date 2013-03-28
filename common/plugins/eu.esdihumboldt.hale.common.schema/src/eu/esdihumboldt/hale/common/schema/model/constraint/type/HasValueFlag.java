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
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Flags if a type has a direct value, apart from eventual properties, defined
 * by the schema. By default enabled for {@link TypeDefinition}s that have no
 * properties, otherwise disabled by default.<br>
 * <br>
 * The {@link Binding} constraint defines the Java binding for the value.
 * 
 * @see Binding
 * @see AugmentedValueFlag
 * 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public class HasValueFlag extends AbstractFlagConstraint implements TypeConstraint {

	/**
	 * Enabled has value flag
	 */
	public static final HasValueFlag ENABLED = new HasValueFlag(true);

	/**
	 * Disabled has value flag
	 */
	public static final HasValueFlag DISABLED = new HasValueFlag(false);

	/**
	 * Get the simple type flag
	 * 
	 * @param hasValue if the flag shall be enabled
	 * @return the flag
	 */
	public static HasValueFlag get(boolean hasValue) {
		return (hasValue) ? (ENABLED) : (DISABLED);
	}

	/**
	 * Creates a default simple type flag, which is disabled. If possible,
	 * instead of creating an instance, use {@link #ENABLED} or
	 * {@link #DISABLED}.
	 * 
	 * @see Constraint
	 */
	public HasValueFlag() {
		this(false);
	}

	/**
	 * Creates a default simple type flag from a type definition. The flag is
	 * enabled if the type has no properties.
	 * 
	 * @param typeDef the type definition
	 * 
	 * @see Constraint
	 */
	public HasValueFlag(TypeDefinition typeDef) {
		this(typeDef.getChildren().isEmpty());
	}

	/**
	 * @see AbstractFlagConstraint#AbstractFlagConstraint(boolean)
	 */
	protected HasValueFlag(boolean enabled) {
		super(enabled);
	}

	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// if a type has a value, its sub-type has a value too
		return true;
	}

}
