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

package eu.esdihumboldt.hale.common.schema.model.constraint.type;

import java.util.HashMap;
import java.util.Map;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import net.jcip.annotations.Immutable;

/**
 * Specifies a Java binding for a type value, default binding is {@link Object}.
 * <br>
 * <br>
 * The binding is usually only relevant when the {@link HasValueFlag} is enabled
 * for a type.
 * 
 * @see HasValueFlag
 * 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public class Binding implements TypeConstraint {

	private static final ALogger log = ALoggerFactory.getLogger(Binding.class);

	/**
	 * Binding singletons, binding class mapped to the corresponding binding
	 * constraint.
	 */
	private static final Map<Class<?>, Binding> singletons = new HashMap<Class<?>, Binding>();

	/**
	 * Get the binding constraint with the given Java binding
	 * 
	 * @param binding the type's Java binding
	 * @return the binding constraint (which is a singleton)
	 */
	public static Binding get(Class<?> binding) {
		Binding bc = singletons.get(binding);
		if (bc == null) {
			if (binding.isPrimitive()) {
				// warn about primitive binding, as it may not have a null value
				log.warn("Using a primitive type as binding is discouraged");
			}

			bc = new Binding(binding);
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
	public Binding() {
		this(Object.class);
	}

	/**
	 * Creates a constraint with the given binding
	 * 
	 * @param binding the Java binding
	 */
	private Binding(Class<?> binding) {
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

	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// inherit unless overridden
		return true;
	}

}
