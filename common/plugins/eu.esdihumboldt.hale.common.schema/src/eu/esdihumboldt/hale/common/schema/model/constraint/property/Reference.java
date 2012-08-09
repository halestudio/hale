/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.schema.model.constraint.property;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.PrimaryKey;

/**
 * Specifies that a property references another another type's {@link PrimaryKey}.
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
