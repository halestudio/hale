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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.PrimaryKey;

/**
 * Specifies that a property references another identifiers of other objects,
 * e.g. the {@link PrimaryKey}.
 * 
 * @author Kai Schwierczek
 * @author Simon Templer
 */
@Constraint(mutable = true)
public class Reference implements PropertyConstraint {

	/**
	 * The referenced target types. A <code>null</code> value stands for unknown
	 * referenced target types.
	 */
	private Set<TypeDefinition> referencedTypes;

	/**
	 * States if the associated property is classified as reference.
	 */
	private boolean reference;

	/**
	 * Creates a default "reference" that references nothing.
	 */
	public Reference() {
		this(false);
	}

	/**
	 * Create a reference constraint.
	 * 
	 * @param reference <code>true</code>, if the property should be marked as
	 *            reference, w/o specifying specific target types,
	 *            <code>false</code> if it should not be marked as reference
	 */
	public Reference(boolean reference) {
		this.reference = reference;
	}

	/**
	 * Creates a reference to the specified type.
	 * 
	 * @param targetType the type that gets referenced
	 */
	public Reference(TypeDefinition targetType) {
		this();
		addReferencedType(targetType);
	}

	/**
	 * Get the types of objects that may be associated through the reference.
	 * 
	 * @return the referenced types, may be <code>null</code> if unknown
	 */
	public Collection<? extends TypeDefinition> getReferencedTypes() {
		if (referencedTypes == null) {
			return null;
		}
		else {
			return Collections.unmodifiableSet(referencedTypes);
		}
	}

	/**
	 * Add a referenced type. Marks the property explicitly as reference.
	 * 
	 * @param type the referenced type to add
	 */
	public void addReferencedType(TypeDefinition type) {
		if (referencedTypes == null) {
			referencedTypes = new HashSet<>();
		}
		referencedTypes.add(type);
		reference = true;
	}

	/**
	 * Returns whether this reference references anything.
	 * 
	 * @return true, if this reference references anything, false otherwise
	 */
	public boolean isReference() {
		return reference;
	}

	/**
	 * Extract the identifier of a referenced object from a reference value. The
	 * default implementation just returns the given reference.
	 * 
	 * @param refValue the reference
	 * @return the identifier of the referenced object
	 */
	public Object extractId(Object refValue) {
		return refValue;
	}

}
