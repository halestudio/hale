/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.cst.functions.groovy.internal;

import groovy.lang.DelegatingMetaClass;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;

/**
 * Metaclass that prefers delegating method calls to {@link GroovyObject}s
 * instead of trying to invoke the method in the meta class first.
 * 
 * This allows the {@link GroovyObject} overriding implementations in the meta
 * class, for instance the default methods that Groovy adds to all objects like
 * <code>findAll</code> or <code>each</code>.
 * 
 * Attention: If the {@link GroovyObject} itself delegates calls to the meta
 * class there is a loop resulting in a stack overflow. Thus select careful for
 * which classes/objects you use this metaclass.
 * 
 * @author Simon Templer
 */
public class GroovyObjectMetaClass extends DelegatingMetaClass {

	/**
	 * @see DelegatingMetaClass#DelegatingMetaClass(Class)
	 */
	public GroovyObjectMetaClass(@SuppressWarnings("rawtypes") Class theClass) {
		super(theClass);
		initialize();
	}

	/**
	 * @see DelegatingMetaClass#DelegatingMetaClass(MetaClass)
	 */
	public GroovyObjectMetaClass(MetaClass delegate) {
		super(delegate);
		initialize();
	}

	@Override
	public Object invokeMethod(Object object, String methodName, Object[] arguments) {
		if (object instanceof GroovyObject) {
			// Prefer the GroovyObject implementation over an implementation in
			// the meta class.
			// Allows delegating Groovy default methods like findAll to the
			// Groovy object.
			try {
				return ((GroovyObject) object).invokeMethod(methodName, arguments);
			} catch (MissingMethodException | StackOverflowError e) {
				// recover calling original meta class
				return super.invokeMethod(object, methodName, arguments);
			}
		}
		else {
			return super.invokeMethod(object, methodName, arguments);
		}
	}

}
