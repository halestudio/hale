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

package eu.esdihumboldt.hale.schema.model.constraints.type;

import java.util.HashMap;
import java.util.Map;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.schema.model.Constraint;
import eu.esdihumboldt.hale.schema.model.TypeConstraint;

/**
 * Specifies a Java binding for a type, default binding is {@link Object}. 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public class BindingConstraint implements TypeConstraint {
	
	/**
	 * Binding singletons, binding class mapped to the corresponding binding
	 * constraint.
	 */
	private static final Map<Class<?>, BindingConstraint> singletons = new HashMap<Class<?>, BindingConstraint>();
	
	/**
	 * Get the binding constraint with the given Java binding
	 * 
	 * @param binding the type's Java binding
	 * @return the binding constraint (which is a singleton)
	 */
	public static BindingConstraint getBinding(Class<?> binding) {
		BindingConstraint bc = singletons.get(binding);
		if (bc == null) {
			bc = new BindingConstraint(binding);
			singletons.put(binding, bc);
		}
		return bc;
	}

	/**
	 * The binding
	 */
	private final Class<?> binding;

	/**
	 * Creates a default binding constraint with {@link Object} binding.
	 * 
	 * @see Constraint 
	 */
	public BindingConstraint() {
		this(Object.class);
	}

	/**
	 * Creates a constraint with the given binding
	 * @param binding the Java binding
	 */
	private BindingConstraint(Class<?> binding) {
		super();
		
		this.binding = binding;
	}
	
	/**
	 * Get the Java binding of the type
	 * 
	 * @return the binding
	 */
	public Class<?> getBinding() {
		return binding;
	}
	
}
