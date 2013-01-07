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

package eu.esdihumboldt.hale.common.schema.model.constraint;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * Helper for creating default constraints and dealing with the
 * {@link Constraint} annotation. Caches immutable default constraints that have
 * a default constructor.
 * 
 * @see Constraint
 * @see Definition#getConstraint(Class)
 * 
 * @author Simon Templer
 */
public abstract class ConstraintUtil {

	private static final ALogger log = ALoggerFactory.getLogger(ConstraintUtil.class);

	private static final Map<Class<?>, Object> cachedDefaults = new HashMap<Class<?>, Object>();

	/**
	 * Get the default constraint for the given constraint type.
	 * 
	 * @param <T> the constraint type
	 * 
	 * @param constraintType the concrete constraint type, i.e. a type annotated
	 *            with {@link Constraint} and defining a default constructor
	 *            and/or a constructor taking a {@link Definition} as an
	 *            argument
	 * @param definition the definition the constraint will be associated to
	 * @return the default constraint of the given type
	 * @throws IllegalArgumentException if the given type is no constraint type
	 *             or creating the default constraint fails
	 * 
	 * @see Constraint
	 * @see Definition#getConstraint(Class)
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getDefaultConstraint(Class<T> constraintType, Definition<?> definition)
			throws IllegalArgumentException {
		if (!constraintType.isAnnotationPresent(Constraint.class)) {
			throw new IllegalArgumentException("The type " + constraintType.getName()
					+ " is no constraint type.");
		}

		Object cached = cachedDefaults.get(constraintType);
		if (cached != null) {
			return (T) cached;
		}

		// try the definition constructors
		Queue<Class<? extends Definition<?>>> defTypes = new LinkedList<Class<? extends Definition<?>>>();
		defTypes.add((Class<? extends Definition<?>>) definition.getClass());

		while (!defTypes.isEmpty()) {
			Class<? extends Definition<?>> defType = defTypes.poll();
			try {
				// try creation
				Constructor<T> defConstructor = constraintType.getConstructor(defType);
				T constraint = defConstructor.newInstance(definition);
				return constraint;
			} catch (Throwable e) {
				// ignore, try other constructor
				log.debug("Could not create default constraint using a definition constructor", e);
			}

			// add supertype to queue
			Class<?> superType = defType.getSuperclass();
			if (superType != null && Definition.class.isAssignableFrom(superType)) {
				defTypes.add((Class<? extends Definition<?>>) superType);
			}

			// add interfaces to queue
			for (Class<?> intfc : defType.getInterfaces()) {
				// TODO also check if interface has been checked already?
				if (Definition.class.isAssignableFrom(intfc)) {
					defTypes.add((Class<? extends Definition<?>>) intfc);
				}
			}
		}

		// try the default constructor
		try {
			Constructor<T> defConstructor = constraintType.getConstructor();
			T constraint = defConstructor.newInstance();
			if (!isMutableConstraint(constraintType)) {
				// constraint may be cached
				cachedDefaults.put(constraintType, constraint);
			}
			return constraint;
		} catch (Throwable e) {
			// ignore, try other constructor
			log.debug("Could not create default constraint using the default constructor", e);
		}

		throw new IllegalArgumentException(
				"Could not create a default constraint for constraint type "
						+ constraintType.getSimpleName()
						+ ". Ensure that a concrete constraint type is given and "
						+ "that the implementation adheres to the contract specified by Constraint");
	}

	/**
	 * Determine the constraint type in the hierarchy of the given type, i.e.
	 * the type that is marked with {@link Constraint}
	 * 
	 * @param type the type to determine the constraint type for
	 * @return the constraint type
	 * @throws IllegalArgumentException if no constraint type exists in the type
	 *             hierarchy
	 */
	public static Class<?> getConstraintType(Class<?> type) throws IllegalArgumentException {
		while (!type.isAnnotationPresent(Constraint.class)) {
			if (type.equals(Object.class)) {
				throw new IllegalArgumentException("The type " + type.getName()
						+ " has no constraint type in its hierarchy.");
			}

			type = type.getSuperclass();
		}

		return type;
	}

	/**
	 * Determine if the constraint type in the hierarchy of the given type is
	 * mutable.
	 * 
	 * @param type the type to determine the constraint type for
	 * @return if the constraint is mutable
	 * @throws IllegalArgumentException if no constraint type exists in the type
	 *             hierarchy
	 */
	public static boolean isMutableConstraint(Class<?> type) {
		type = getConstraintType(type);

		Constraint constraint = type.getAnnotation(Constraint.class);
		return constraint.mutable();
	}

}
