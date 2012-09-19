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

package eu.esdihumboldt.hale.common.instance.extension.validation;

import java.util.Hashtable;
import java.util.Map;

/**
 * Context for instance validation.
 * 
 * @author Kai Schwierczek
 */
public class InstanceValidationContext {

	// XXX The context could be extended to provide another context map
	// for every instance. The current map is a context for a whole validation
	// run.

	private final Map<Class<? extends ConstraintValidator>, Object> contextMap;

	/**
	 * Constructs a new context object.
	 */
	public InstanceValidationContext() {
		contextMap = new Hashtable<Class<? extends ConstraintValidator>, Object>();
	}

	/**
	 * Returns the object associated with the given validator class.
	 * 
	 * @param validatorClass the validator class
	 * @return the associated object or <code>null</code> if none was set
	 */
	public Object getContext(Class<? extends ConstraintValidator> validatorClass) {
		return contextMap.get(validatorClass);
	}

	/**
	 * Returns true, if and only if there is a context object present for the
	 * given validator class.
	 * 
	 * @param validatorClass the validator class
	 * @return true, if and only if there is a context object present for the
	 *         given validator class
	 */
	public boolean containsContext(Class<? extends ConstraintValidator> validatorClass) {
		return contextMap.containsKey(validatorClass);
	}

	/**
	 * Sets the given object as the context object for the given validator
	 * class.<br>
	 * The object may not be <code>null</code>.
	 * 
	 * @param validatorClass the validator class
	 * @param contextObject the new associated object, may not be
	 *            <code>null</code>
	 */
	public void putContext(Class<? extends ConstraintValidator> validatorClass, Object contextObject) {
		contextMap.put(validatorClass, contextObject);
	}
}
