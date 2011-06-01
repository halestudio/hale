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

package eu.esdihumboldt.hale.schema.model.constraints;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.schema.model.Constraint;
import eu.esdihumboldt.hale.schema.model.Definition;

/**
 * Helper for creating default constraints. Caches immutable default constraints
 * that have a default constructor.
 * @see Constraint
 * @see Definition#getConstraint(Class)
 * 
 * @author Simon Templer
 */
public abstract class DefaultConstraints {
	
	private static final ALogger log = ALoggerFactory.getLogger(DefaultConstraints.class);
	
	private static final Map<Class<? extends Constraint>, Constraint> cachedDefaults = new HashMap<Class<? extends Constraint>, Constraint>();

	/**
	 * Get the default constraint for the given constraint type.
	 * @param <T> the constraint type
	 * 
	 * @param constraintType the concrete constraint class
	 * @param definition the definition the constraint will be associated to
	 * @return the default constraint of the given type
	 * @throws IllegalArgumentException if creating the default constraint fails 
	 * 
	 * @see Definition#getConstraint(Class)
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Constraint> T getDefaultConstraint(
			Class<T> constraintType, Definition<?> definition) throws IllegalArgumentException {
		Constraint cached = cachedDefaults.get(constraintType);
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
				//TODO also check if interface has been checked already?
				if (Definition.class.isAssignableFrom(intfc)) {
					defTypes.add((Class<? extends Definition<?>>) intfc);
				}
			}
		}
		
		// try the default constructor
		try {
			Constructor<T> defConstructor = constraintType.getConstructor();
			T constraint = defConstructor.newInstance();
			if (!constraint.isMutable()) {
				// constraint may be cached
				cachedDefaults.put(constraintType, constraint);
			}
			return constraint;
		} catch (Throwable e) {
			// ignore, try other constructor
			log.debug("Could not create default constraint using the default constructor", e);
		}
		
		throw new IllegalArgumentException("Could not create a default constraint for constraint type " + 
				constraintType.getSimpleName() + ". Ensure that a concrete constraint type is given and " + 
				"that the implementation adheres to the contract specified by Constraint");
	}
	
}
