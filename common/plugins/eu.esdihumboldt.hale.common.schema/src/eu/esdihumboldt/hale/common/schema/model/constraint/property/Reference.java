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
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.PrimaryKey;

/**
 * Specifies that a property references another another type's
 * {@link PrimaryKey}.
 * 
 * @author Kai Schwierczek
 */
@Immutable
@Constraint(mutable = false)
public class Reference implements PropertyConstraint {

	private final TypeDefinition targetType;

	/**
	 * Creates a default "reference" that references nothing.
	 */
	public Reference() {
		targetType = null;
	}

	/**
	 * Creates a reference to the specified type.
	 * 
	 * @param targetType the type that gets referenced
	 */
	public Reference(TypeDefinition targetType) {
		this.targetType = targetType;
	}

	/**
	 * Returns whether this reference references anything.
	 * 
	 * @return true, if this reference references anything, false otherwise
	 */
	public boolean isReference() {
		return targetType != null;
	}

	/**
	 * Returns the referenced type. This is <code>null</code> if and only if
	 * {@link #isReference()} is false.
	 * 
	 * @return the referenced type, may be <code>null</code>
	 */
	public TypeDefinition getTargetType() {
		return targetType;
	}
}
