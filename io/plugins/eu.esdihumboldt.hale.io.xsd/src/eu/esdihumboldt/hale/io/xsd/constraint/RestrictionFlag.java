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
