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

package eu.esdihumboldt.hale.common.schema.model.constraint;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;

/**
 * Constraint that can provide a custom display name. By default has no custom
 * name.
 * 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public class DisplayName implements GroupPropertyConstraint, PropertyConstraint, TypeConstraint {

	private final String customName;

	/**
	 * Create a default constraint that has no custom name.
	 */
	public DisplayName() {
		this(null);
	}

	/**
	 * Create a custom display name constraint
	 * 
	 * @param customName the custom display name
	 */
	public DisplayName(String customName) {
		super();
		this.customName = customName;
	}

	/**
	 * Get the custom display name.
	 * 
	 * @return the customName the custom display name or <code>null</code> if
	 *         there is no custom name
	 */
	public String getCustomName() {
		return customName;
	}

	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// no inheritance for display names
		return false;
	}

}
