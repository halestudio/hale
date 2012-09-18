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
