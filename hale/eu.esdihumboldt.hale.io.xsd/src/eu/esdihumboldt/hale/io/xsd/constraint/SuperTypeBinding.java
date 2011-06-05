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

package eu.esdihumboldt.hale.io.xsd.constraint;

import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.constraint.type.Binding;

/**
 * Binding constraint that uses the binding of the super type if possible.
 * @author Simon Templer
 */
public class SuperTypeBinding extends Binding {

	private final TypeDefinition type;
	
	/**
	 * Create a binding constraint that uses the binding of the super type
	 * of the given type if possible.
	 * 
	 * @param type the type to which the binding is associated
	 */
	public SuperTypeBinding(TypeDefinition type) {
		super();
		this.type = type;
	}

	/**
	 * @see Binding#getBinding()
	 */
	@Override
	public Class<?> getBinding() {
		TypeDefinition superType = type.getSuperType();
		
		if (superType != null) {
			return superType.getConstraint(Binding.class).getBinding();
		}
		
		return super.getBinding();
	}

}
