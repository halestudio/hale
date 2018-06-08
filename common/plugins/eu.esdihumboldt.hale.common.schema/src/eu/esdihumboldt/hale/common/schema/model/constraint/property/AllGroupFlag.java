/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */
package eu.esdihumboldt.hale.common.schema.model.constraint.property;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;
import net.jcip.annotations.Immutable;

/**
 * Flags if a property group is an "all" group that supports children to appear
 * in any order.
 * 
 * @author Florian Esser
 */
@Immutable
@Constraint(mutable = false)
public class AllGroupFlag extends AbstractFlagConstraint implements GroupPropertyConstraint {

	/**
	 * Enabled all group flag
	 */
	public static final AllGroupFlag ENABLED = new AllGroupFlag(true);

	/**
	 * Disabled all group flag
	 */
	public static final AllGroupFlag DISABLED = new AllGroupFlag(false);

	/**
	 * Get the all group flag
	 * 
	 * @param isAllGroup if the flag shall be enabled
	 * @return the flag
	 */
	public static AllGroupFlag get(boolean isAllGroup) {
		return (isAllGroup) ? (ENABLED) : (DISABLED);
	}

	/**
	 * Creates a default flag, which is disabled. If possible, instead of
	 * creating an instance, use {@link #get(boolean)}, {@link #ENABLED} or
	 * {@link #DISABLED}.
	 * 
	 * @see Cardinality
	 */
	public AllGroupFlag() {
		this(false);
	}

	/**
	 * @see AbstractFlagConstraint#AbstractFlagConstraint(boolean)
	 */
	private AllGroupFlag(boolean enabled) {
		super(enabled);
	}
}
