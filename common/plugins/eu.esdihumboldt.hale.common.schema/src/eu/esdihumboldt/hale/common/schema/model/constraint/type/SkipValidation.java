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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.schema.model.constraint.type;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Specifies if validation should be skipped for a property value. May be
 * associated to a property type. Defaults to the validation not being skipped.
 * Not inheritable by default.
 * 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public class SkipValidation extends AbstractFlagConstraint implements TypeConstraint {

	/**
	 * Enabled skip validation flag
	 */
	public static final SkipValidation ENABLED = new SkipValidation(true);

	/**
	 * Disabled skip validation flag
	 */
	public static final SkipValidation DISABLED = new SkipValidation(false);

	/**
	 * Get the skip validation constraint
	 * 
	 * @param skipValidation if the validation should be generally skipped
	 * @return the constraint instance
	 */
	public static SkipValidation get(boolean skipValidation) {
		return (skipValidation) ? (ENABLED) : (DISABLED);
	}

	/**
	 * Creates a default skip validation constraint, which is disabled. If
	 * possible, instead of creating an instance, use {@link #get(boolean)},
	 * {@link #ENABLED} or {@link #DISABLED}.
	 */
	public SkipValidation() {
		this(false);
	}

	/**
	 * Creates a skip validation constraint
	 * 
	 * @param enabled if validation should be generally skipped for the
	 *            associated property
	 */
	protected SkipValidation(boolean enabled) {
		super(enabled);
	}

	/**
	 * Determines if validation should be skipped for a property and its
	 * children for the given property value
	 * 
	 * @param propertyValue the property value, may be an Instance
	 * @return if validation should be skipped for the property and its children
	 */
	public boolean skipValidation(Object propertyValue) {
		return isEnabled();
	}

	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		return false;
	}

}
