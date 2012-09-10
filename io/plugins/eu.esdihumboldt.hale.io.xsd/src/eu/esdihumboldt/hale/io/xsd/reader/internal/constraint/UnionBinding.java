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

package eu.esdihumboldt.hale.io.xsd.reader.internal.constraint;

import java.util.Collection;
import java.util.Iterator;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;

/**
 * Binding constraint for type unions
 * 
 * @author Simon Templer
 */
public class UnionBinding extends Binding {

	private Collection<? extends TypeDefinition> unionTypes;

	/**
	 * Create a type union binding constraint
	 * 
	 * @param unionTypes the definitions of the types contained in the union
	 */
	public UnionBinding(Collection<? extends TypeDefinition> unionTypes) {
		this.unionTypes = unionTypes;
	}

	/**
	 * @see Binding#getBinding()
	 */
	@Override
	public Class<?> getBinding() {
		Iterator<? extends TypeDefinition> it = unionTypes.iterator();

		if (it.hasNext()) {
			// combine bindings from union types
			Class<?> binding = it.next().getConstraint(Binding.class).getBinding();

			while (it.hasNext()) {
				binding = findCompatibleClass(binding, it.next().getConstraint(Binding.class)
						.getBinding());
			}

			return binding;
		}

		return super.getBinding();
	}

	private static Class<?> findCompatibleClass(Class<?> binding, Class<?> binding2) {
		if (binding == null || binding2 == null) {
			return Object.class;
		}

		if (binding.equals(binding2)) {
			return binding;
		}
		else if (binding.isAssignableFrom(binding2)) {
			return binding;
		}
		else if (binding2.isAssignableFrom(binding)) {
			return binding2;
		}
		// special treatment for string - if any binding is compatible to
		// String, it is returned
		else if (String.class.isAssignableFrom(binding) || String.class.isAssignableFrom(binding2)) {
			return String.class;
		}
		else {
			return findCompatibleClass(binding.getSuperclass(), binding2.getSuperclass());
		}
	}

}
