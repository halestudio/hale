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

package eu.esdihumboldt.hale.common.schema.model.constraint.property;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Flags if a property is nillable, disabled by default
 * 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public class NillableFlag extends AbstractFlagConstraint implements PropertyConstraint {

	/**
	 * Enabled nillable flag
	 */
	public static final NillableFlag ENABLED = new NillableFlag(true);

	/**
	 * Disabled nillable flag
	 */
	public static final NillableFlag DISABLED = new NillableFlag(false);

	/**
	 * Get the nillable flag
	 * 
	 * @param isNillable if the flag shall be enabled
	 * @return the flag
	 */
	public static NillableFlag get(boolean isNillable) {
		return (isNillable) ? (ENABLED) : (DISABLED);
	}

	/**
	 * Creates a default nillable flag, which is disabled. If possible, instead
	 * of creating an instance, use {@link #get(boolean)}, {@link #ENABLED} or
	 * {@link #DISABLED}.
	 * 
	 * @see Cardinality
	 */
	public NillableFlag() {
		this(false);
	}

	/**
	 * @see AbstractFlagConstraint#AbstractFlagConstraint(boolean)
	 */
	private NillableFlag(boolean enabled) {
		super(enabled);
	}
}
