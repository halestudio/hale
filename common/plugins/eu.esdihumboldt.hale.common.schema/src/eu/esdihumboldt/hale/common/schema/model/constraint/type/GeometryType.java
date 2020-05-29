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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.jcip.annotations.Immutable;

import org.locationtech.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Specifies the geometry type for properties with a {@link GeometryProperty}
 * binding.
 * 
 * @author Simon Templer
 * @since 2.5
 */
@Immutable
@Constraint(mutable = false)
public class GeometryType implements TypeConstraint {

	/**
	 * Geometry binding singletons, binding class mapped to the corresponding
	 * geometry type constraint. Defaults to the type binding as geometry
	 * binding if it is a {@link Geometry} binding or to {@link Geometry} if it
	 * is a {@link GeometryProperty} binding.
	 */
	private static final Map<Class<? extends Geometry>, GeometryType> singletons = new HashMap<Class<? extends Geometry>, GeometryType>();

	/**
	 * Get the geometry type constraint with the given JTS geometry binding.
	 * 
	 * @param binding the type's geometry binding
	 * @return the binding constraint (which is a singleton)
	 */
	public static GeometryType get(Class<? extends Geometry> binding) {
		GeometryType bc = singletons.get(binding);
		if (bc == null) {
			bc = new GeometryType(binding, null);
			singletons.put(binding, bc);
		}
		return bc;
	}

	/**
	 * The geometry binding
	 */
	private final Class<? extends Geometry> binding;

	/**
	 * The type the constraint is associated to, may be <code>null</code> if
	 * unknown.
	 */
	private final TypeDefinition type;

	/**
	 * Creates a default geometry constraint classifying the type as being no
	 * geometry type.
	 * 
	 * @see Constraint
	 */
	public GeometryType() {
		this(null);
	}

	/**
	 * Creates a geometry type constraint that determines the geometry binding
	 * from the given type definition.
	 * 
	 * @param type the type definition
	 */
	public GeometryType(TypeDefinition type) {
		this(null, type);
	}

	/**
	 * Creates a constraint with the given geometry binding
	 * 
	 * @param binding the JTS geometry binding
	 * @param type the definition of the type the constraint is associated to,
	 *            may be <code>null</code>
	 */
	private GeometryType(Class<? extends Geometry> binding, TypeDefinition type) {
		super();

		this.binding = binding;
		this.type = type;
	}

	/**
	 * Get the geometry binding of the type.
	 * 
	 * @return the binding, <code>null</code> if it is no geometry
	 * 
	 * @see #isGeometry()
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends Geometry> getBinding() {
		if (binding == null && type != null) {
			// determine from type

			// check binding
			Binding binding = type.getConstraint(Binding.class);
			if (Geometry.class.isAssignableFrom(binding.getBinding())) {
				return (Class<? extends Geometry>) binding.getBinding();
			}
			if (Collection.class.isAssignableFrom(binding.getBinding())) {
				// check element type
				ElementType elementType = type.getConstraint(ElementType.class);
				if (Geometry.class.isAssignableFrom(elementType.getBinding())) {
					return (Class<? extends Geometry>) elementType.getBinding();
				}
			}

			if (isGeometry()) {
				// cases for which the exact geometry type can't be determined
				return Geometry.class;
			}
		}

		return binding;
	}

	/**
	 * Specifies if the type the constraint is associated to is a geometry type
	 * (meaning it has a {@link GeometryProperty} or {@link Geometry} value).
	 * 
	 * @return if the type is a geometry type
	 */
	public boolean isGeometry() {
		if (binding == null && type != null) {
			// determine from type

			// check binding
			Binding binding = type.getConstraint(Binding.class);
			if (Geometry.class.isAssignableFrom(binding.getBinding())
					|| GeometryProperty.class.isAssignableFrom(binding.getBinding())) {
				return true;
			}
			if (Collection.class.isAssignableFrom(binding.getBinding())) {
				// check element type
				ElementType elementType = type.getConstraint(ElementType.class);
				if (Geometry.class.isAssignableFrom(elementType.getBinding())
						|| GeometryProperty.class.isAssignableFrom(elementType.getBinding())) {
					return true;
				}
			}
		}

		return binding != null;
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
