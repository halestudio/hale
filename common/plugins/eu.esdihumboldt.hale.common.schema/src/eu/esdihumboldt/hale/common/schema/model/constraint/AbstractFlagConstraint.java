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

/**
 * Abstract constraint that represents a flag that can be enabled or disabled
 * 
 * @author Simon Templer
 */
@Immutable
public abstract class AbstractFlagConstraint {

	private final boolean enabled;

	/**
	 * Create a flag that is enabled or disabled
	 * 
	 * @param enabled if the flag is enabled
	 */
	protected AbstractFlagConstraint(boolean enabled) {
		super();

		this.enabled = enabled;
	}

	/**
	 * States if the flag is enabled
	 * 
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (enabled ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractFlagConstraint other = (AbstractFlagConstraint) obj;
		if (enabled != other.enabled)
			return false;
		return true;
	}

}
