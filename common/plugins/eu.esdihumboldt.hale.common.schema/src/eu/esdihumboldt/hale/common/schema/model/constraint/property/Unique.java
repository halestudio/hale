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

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;

/**
 * Specifies that a property should be unique.
 * 
 * @author Kai Schwierczek
 */
@Constraint(mutable = false)
public class Unique implements PropertyConstraint {

	private final String identifier;

	/**
	 * Creates a default unique object that is disabled.
	 */
	public Unique() {
		identifier = null;
	}

	/**
	 * Creates a unique flag with the given identifier.
	 * 
	 * @param identifier the unique identifier. Unique constraints with the same
	 *            identifier share a unique context. <code>null</code> means not
	 *            unique at all.
	 */
	public Unique(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Returns whether the unique constraint is enabled, the identifier is not
	 * null.
	 * 
	 * @return whether the unique constraint is enabled
	 */
	public boolean isEnabled() {
		return identifier != null;
	}

	/**
	 * Returns the unique identifer.
	 * 
	 * @return the unique identifer
	 */
	public String getIdentifier() {
		return identifier;
	}
}
