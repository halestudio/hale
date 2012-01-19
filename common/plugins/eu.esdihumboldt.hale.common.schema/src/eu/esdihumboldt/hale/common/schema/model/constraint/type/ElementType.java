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

import java.util.HashMap;
import java.util.Map;

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
	 * ElementType singletons, element binding class mapped to the corresponding
	 * ElementType constraint.
	 */
	private static final Map<Class<?>, ElementType> singletons = new HashMap<Class<?>, ElementType>();
	
	/**
	 * Get the element type constraint with the given Java binding
	 * 
	 * @param binding the type's Java binding
	 * @return the element type constraint (which is a singleton)
	 */
	public static ElementType get(Class<?> binding) {
		ElementType et = singletons.get(binding);
		if (et == null) {
			et = new ElementType(binding);
			singletons.put(binding, et);
		}
		return et;
	}
	
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
	 * The element type binding. May be <code>null</code>.
	 */
	private final Class<?> binding;

	/**
	 * Creates a default element binding constraint with {@link Object} binding
	 * and no type definition.
	 * @see Constraint 
	 */
	public ElementType() {
		this(Object.class);
	}

	/**
	 * Creates a constraint with the given type definition
	 * @param elementType the element type
	 */
	private ElementType(TypeDefinition elementType) {
		super();
		
		this.definition = elementType;
		this.binding = null;
	}
	
	/**
	 * Creates an element type with the given binding.
	 * @param binding the element type binding
	 */
	private ElementType(Class<?> binding) {
		super();
		
		this.binding = binding;
		this.definition = null;
	}
	
	/**
	 * Get the Java binding for collection elements of the type
	 * @return the element binding
	 */
	public Class<?> getBinding() {
		if (definition == null) {
			return binding;
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
	
	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// inherit unless overridden
		return true;
	}
	
}
