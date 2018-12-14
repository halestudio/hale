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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Specifies that a property represents a reference, where the actual reference
 * value is stored in a child.
 * 
 * @author Simon Templer
 */
@Constraint(mutable = true)
public class ReferenceProperty implements PropertyConstraint, ReferenceLike {

	/**
	 * The referenced target types. A <code>null</code> value stands for unknown
	 * referenced target types.
	 */
	private Set<TypeDefinition> referencedTypes;

	/**
	 * States if the associated property is classified as reference.
	 */
	private boolean reference;

	private final List<QName> valueProperty;

	/**
	 * Creates a default "reference" that references nothing.
	 */
	public ReferenceProperty() {
		this(false);
	}

	/**
	 * Create a reference constraint.
	 * 
	 * @param reference <code>true</code>, if the property should be marked as
	 *            reference, w/o specifying specific target types,
	 *            <code>false</code> if it should not be marked as reference
	 */
	public ReferenceProperty(boolean reference) {
		this.reference = reference;
		this.valueProperty = null;
	}

	/**
	 * Create a constraint representing a reference.
	 * 
	 * @param valuePath the path to the property that holds the actual reference
	 *            value
	 */
	public ReferenceProperty(List<QName> valuePath) {
		this.reference = true;
		this.valueProperty = valuePath == null ? null : Collections.unmodifiableList(valuePath);
	}

	/**
	 * Creates a reference to the specified type.
	 * 
	 * @param valuePath the path to the property that holds the actual reference
	 *            value
	 * @param targetType the type that gets referenced
	 */
	public ReferenceProperty(List<QName> valuePath, TypeDefinition targetType) {
		this(valuePath);
		if (targetType != null) {
			addReferencedType(targetType);
		}
	}

	@Override
	public Collection<? extends TypeDefinition> getReferencedTypes() {
		if (referencedTypes == null) {
			return null;
		}
		else {
			return Collections.unmodifiableSet(referencedTypes);
		}
	}

	@Override
	public void addReferencedType(TypeDefinition type) {
		if (referencedTypes == null) {
			referencedTypes = new HashSet<>();
		}
		referencedTypes.add(type);
		reference = true;
	}

	@Override
	public boolean isReference() {
		return reference;
	}

	/**
	 * Get the path to the property that holds the reference value.
	 * 
	 * @return the path to the property or <code>null</code>
	 */
	public List<QName> getValueProperty() {
		return valueProperty;
	}

}
