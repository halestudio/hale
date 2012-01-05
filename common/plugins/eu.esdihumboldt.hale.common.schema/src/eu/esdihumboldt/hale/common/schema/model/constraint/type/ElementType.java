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

package eu.esdihumboldt.hale.common.schema.model.constraint.type;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Specifies a Java binding and optionally a {@link TypeDefinition} for a 
 * elements of a collection. This is only relevant for a type if its 
 * {@link Binding} is a collection or array, default element binding is 
 * {@link Object}. 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public class ElementType implements TypeConstraint {
	
	/**
	 * Create an element type constraint with the given element type. 
	 * @param elementType the element type definition
	 * @return the element type constraint
	 */
	public static ElementType createFromType(TypeDefinition elementType) {
		return new ElementType(elementType);
	}
	
	/**
	 * The element type definition
	 */
	private final TypeDefinition definition;

	/**
	 * Creates a default element binding constraint with {@link Object} binding
	 * and no type definition.
	 * @see Constraint 
	 */
	public ElementType() {
		this(null);
	}

	/**
	 * Creates a constraint with the given type definition
	 * @param elementType the element type
	 */
	private ElementType(TypeDefinition elementType) {
		super();
		
		this.definition = elementType;
	}
	
	/**
	 * Get the Java binding for collection elements of the type
	 * @return the element binding
	 */
	public Class<?> getBinding() {
		if (definition == null) {
			return Object.class;
		}
		return definition.getConstraint(Binding.class).getBinding();
	}

	/**
	 * Get the type definition for collection elements of the type
	 * @return the type definition, may be <code>null</code>
	 */
	public TypeDefinition getDefinition() {
		return definition;
	}
	
}
